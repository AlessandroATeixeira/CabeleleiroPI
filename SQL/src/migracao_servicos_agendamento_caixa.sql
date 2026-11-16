-- MIGRACAO: vincular agendamentos aos serviços cadastrados e calcular
-- automaticamente os valores das movimentações do caixa.
--
-- IMPORTANTE:
-- 1) Faça backup do banco antes de executar.
-- 2) Execute este arquivo no banco existente. NAO execute dml.sql inteiro,
--    pois ele contém DROP DATABASE e recria o banco.
-- 3) Depois do UPDATE, confira a consulta de serviços sem correspondência.
--    Se retornar agendamentos, cadastre os serviços correspondentes e atualize
--    id_servico manualmente antes de continuar com o ALTER TABLE abaixo.

USE bd_cabeleireiro;

-- Cria a coluna de relacionamento sem apagar o texto atual dos agendamentos.
ALTER TABLE agendamento ADD COLUMN id_servico INT NULL AFTER cod_cliente;

-- Relaciona automaticamente os serviços cujo nome corresponde.
UPDATE agendamento a
INNER JOIN servicos s
    ON TRIM(LOWER(s.nm_servico)) = TRIM(LOWER(a.servico))
SET a.id_servico = s.id_servico;

-- ESTA CONSULTA DEVE RETORNAR ZERO LINHAS ANTES DE CONTINUAR.
SELECT a.cod_agendamento, a.servico
FROM agendamento a
WHERE a.id_servico IS NULL;

-- Só execute os comandos abaixo depois de garantir que todos os agendamentos
-- possuem id_servico preenchido.
ALTER TABLE agendamento
    MODIFY id_servico INT NOT NULL,
    ADD CONSTRAINT fk_agendamento_servico
        FOREIGN KEY (id_servico) REFERENCES servicos(id_servico);

ALTER TABLE agendamento DROP COLUMN servico;

DELIMITER $$

DROP PROCEDURE IF EXISTS proc_ins_agendamento$$
DROP PROCEDURE IF EXISTS proc_selc_agendamentos$$
DROP PROCEDURE IF EXISTS proc_upd_agendamento$$
DROP PROCEDURE IF EXISTS proc_del_servico$$
DROP PROCEDURE IF EXISTS proc_ins_caixa$$

CREATE PROCEDURE proc_ins_agendamento(
    IN p_cod INT,
    IN p_cliente INT,
    IN p_id_servico INT,
    IN p_data DATE,
    IN p_horario TIME
)
BEGIN
    IF p_cod IS NULL OR p_cod <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O codigo do agendamento deve ser maior que zero';
    END IF;
    IF p_cliente IS NULL OR NOT EXISTS (SELECT 1 FROM cliente WHERE cod_cliente = p_cliente) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cliente nao encontrado';
    END IF;
    IF p_id_servico IS NULL OR NOT EXISTS (SELECT 1 FROM servicos WHERE id_servico = p_id_servico) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Selecione um servico cadastrado';
    END IF;
    IF p_data IS NULL OR p_horario IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Data e horario sao obrigatorios';
    END IF;
    INSERT INTO agendamento (cod_agendamento, cod_cliente, id_servico, data_agendamento, horario)
    VALUES (p_cod, p_cliente, p_id_servico, p_data, p_horario);
END$$

CREATE PROCEDURE proc_selc_agendamentos()
BEGIN
    SELECT a.cod_agendamento, a.cod_cliente, a.id_servico,
           s.nm_servico AS servico, a.data_agendamento, a.horario
    FROM agendamento a
    INNER JOIN servicos s ON s.id_servico = a.id_servico
    ORDER BY a.data_agendamento, a.horario;
END$$

CREATE PROCEDURE proc_upd_agendamento(
    IN p_cod INT,
    IN p_cliente INT,
    IN p_id_servico INT,
    IN p_data DATE,
    IN p_horario TIME
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM agendamento WHERE cod_agendamento = p_cod) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Agendamento nao encontrado';
    END IF;
    IF p_cliente IS NULL OR NOT EXISTS (SELECT 1 FROM cliente WHERE cod_cliente = p_cliente) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cliente nao encontrado';
    END IF;
    IF p_id_servico IS NULL OR NOT EXISTS (SELECT 1 FROM servicos WHERE id_servico = p_id_servico) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Selecione um servico cadastrado';
    END IF;
    IF p_data IS NULL OR p_horario IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Data e horario sao obrigatorios';
    END IF;
    UPDATE agendamento
    SET cod_cliente = p_cliente, id_servico = p_id_servico,
        data_agendamento = p_data, horario = p_horario
    WHERE cod_agendamento = p_cod;
END$$

CREATE PROCEDURE proc_del_servico(IN p_id INT)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM servicos WHERE id_servico = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Servico nao encontrado';
    END IF;
    IF EXISTS (SELECT 1 FROM caixa WHERE id_servico = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Servico possui movimentacoes de caixa';
    END IF;
    IF EXISTS (SELECT 1 FROM agendamento WHERE id_servico = p_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Servico possui agendamentos associados';
    END IF;
    DELETE FROM servicos WHERE id_servico = p_id;
END$$

-- O valor é calculado pelo banco com base no cadastro:
-- PRODUTO + ENTRADA = valorDeVenda x quantidade
-- PRODUTO + SAIDA   = valorBruto x quantidade
-- SERVICO           = vl_valor do serviço cadastrado
CREATE PROCEDURE proc_ins_caixa(
    IN p_tipo VARCHAR(7),
    IN p_origem VARCHAR(10),
    IN p_cod_produto INT,
    IN p_id_servico INT,
    IN p_quantidade INT,
    IN p_descricao VARCHAR(120),
    IN p_valor DECIMAL(10,2)
)
BEGIN
    DECLARE v_estoque INT DEFAULT NULL;
    DECLARE v_saldo DECIMAL(12,2) DEFAULT 0;
    DECLARE v_valor_unitario DECIMAL(10,2) DEFAULT NULL;
    DECLARE v_valor_total DECIMAL(10,2) DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_tipo IS NULL OR p_tipo NOT IN ('ENTRADA','SAIDA') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tipo invalido: use ENTRADA ou SAIDA';
    END IF;
    IF p_origem IS NULL OR p_origem NOT IN ('PRODUTO','SERVICO') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Origem invalida: use PRODUTO ou SERVICO';
    END IF;
    IF p_descricao IS NULL OR TRIM(p_descricao) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A descricao e obrigatoria';
    END IF;

    START TRANSACTION;

    IF p_origem = 'PRODUTO' THEN
        IF p_id_servico IS NOT NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Informe somente produto para origem PRODUTO';
        END IF;
        IF p_quantidade IS NULL OR p_quantidade <= 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A quantidade deve ser maior que zero';
        END IF;

        SELECT estoque,
               CASE WHEN p_tipo = 'ENTRADA' THEN valorDeVenda ELSE valorBruto END
        INTO v_estoque, v_valor_unitario
        FROM produto WHERE cod_produto = p_cod_produto FOR UPDATE;

        IF v_estoque IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Produto nao encontrado';
        END IF;
        IF p_tipo = 'ENTRADA' AND v_estoque < p_quantidade THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Estoque insuficiente para a venda';
        END IF;
        SET v_valor_total = v_valor_unitario * p_quantidade;
    ELSE
        IF p_id_servico IS NULL OR p_cod_produto IS NOT NULL OR p_quantidade IS NOT NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Selecione um servico valido';
        END IF;
        SELECT vl_valor INTO v_valor_unitario
        FROM servicos WHERE id_servico = p_id_servico;
        IF v_valor_unitario IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Servico nao encontrado';
        END IF;
        SET v_valor_total = v_valor_unitario;
    END IF;

    IF v_valor_total IS NULL OR v_valor_total <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'O valor cadastrado deve ser maior que zero';
    END IF;

    IF p_tipo = 'SAIDA' THEN
        SELECT COALESCE(SUM(CASE WHEN tipo = 'ENTRADA' THEN valor ELSE -valor END), 0)
        INTO v_saldo FROM caixa;
        IF v_valor_total > v_saldo THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A saida nao pode ser maior que o saldo disponivel';
        END IF;
    END IF;

    IF p_origem = 'PRODUTO' THEN
        UPDATE produto
        SET estoque = CASE WHEN p_tipo = 'ENTRADA'
                           THEN estoque - p_quantidade
                           ELSE estoque + p_quantidade END
        WHERE cod_produto = p_cod_produto;

        INSERT INTO caixa (tipo, origem, cod_produto, id_servico, quantidade, descricao, valor)
        VALUES (p_tipo, p_origem, p_cod_produto, NULL, p_quantidade, TRIM(p_descricao), v_valor_total);
    ELSE
        INSERT INTO caixa (tipo, origem, cod_produto, id_servico, quantidade, descricao, valor)
        VALUES (p_tipo, p_origem, NULL, p_id_servico, NULL, TRIM(p_descricao), v_valor_total);
    END IF;

    COMMIT;
END$$

DELIMITER ;
