-- CabeleleiroPI
-- Migracao incremental para vincular lancamentos de servico do caixa a agendamentos.
-- Execute UMA vez no schema bd_cabeleireiro. Nao execute o dml.sql inteiro.

ALTER TABLE caixa
    ADD COLUMN cod_agendamento INT NULL AFTER id_servico,
    ADD CONSTRAINT fk_caixa_agendamento
        FOREIGN KEY (cod_agendamento) REFERENCES agendamento(cod_agendamento),
    ADD CONSTRAINT uq_caixa_agendamento UNIQUE (cod_agendamento);

DROP PROCEDURE IF EXISTS proc_ins_caixa;
DROP PROCEDURE IF EXISTS proc_selc_caixa;
DROP PROCEDURE IF EXISTS proc_selc_agendamentos_disponiveis_caixa;

DELIMITER $$

CREATE PROCEDURE proc_ins_caixa(
    IN p_tipo VARCHAR(7),
    IN p_origem VARCHAR(10),
    IN p_cod_produto INT,
    IN p_id_servico INT,
    IN p_quantidade INT,
    IN p_descricao VARCHAR(120),
    IN p_valor DECIMAL(10,2),
    IN p_cod_agendamento INT
)
BEGIN
    DECLARE v_estoque INT DEFAULT NULL;
    DECLARE v_saldo DECIMAL(12,2) DEFAULT 0;
    DECLARE v_valor_unitario DECIMAL(10,2) DEFAULT NULL;
    DECLARE v_valor_total DECIMAL(10,2) DEFAULT 0;
    DECLARE v_id_servico INT DEFAULT NULL;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_tipo IS NULL OR p_tipo NOT IN ('ENTRADA','SAIDA') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tipo invalido';
    END IF;
    IF p_origem IS NULL OR p_origem NOT IN ('PRODUTO','SERVICO') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Origem invalida';
    END IF;
    IF p_descricao IS NULL OR TRIM(p_descricao) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A descricao e obrigatoria';
    END IF;

    START TRANSACTION;

    IF p_origem = 'PRODUTO' THEN
        IF p_id_servico IS NOT NULL OR p_cod_agendamento IS NOT NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Produto nao utiliza agendamento ou servico';
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
        IF p_cod_agendamento IS NULL OR p_id_servico IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Selecione um agendamento';
        END IF;
        IF p_cod_produto IS NOT NULL OR p_quantidade IS NOT NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Servico nao utiliza produto ou quantidade';
        END IF;
        SELECT a.id_servico, s.vl_valor
          INTO v_id_servico, v_valor_total
          FROM agendamento a
          INNER JOIN servicos s ON s.id_servico = a.id_servico
         WHERE a.cod_agendamento = p_cod_agendamento
         FOR UPDATE;
        IF v_id_servico IS NULL OR v_id_servico <> p_id_servico THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Agendamento ou servico invalido';
        END IF;
        IF EXISTS (SELECT 1 FROM caixa WHERE cod_agendamento = p_cod_agendamento) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Este agendamento ja foi lancado no caixa';
        END IF;
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
           SET estoque = CASE WHEN p_tipo = 'ENTRADA' THEN estoque - p_quantidade
                              ELSE estoque + p_quantidade END
         WHERE cod_produto = p_cod_produto;
    END IF;

    INSERT INTO caixa (tipo, origem, cod_produto, id_servico, cod_agendamento, quantidade, descricao, valor)
    VALUES (p_tipo, p_origem, p_cod_produto, v_id_servico, p_cod_agendamento, p_quantidade, TRIM(p_descricao), v_valor_total);

    COMMIT;
END$$

CREATE PROCEDURE proc_selc_caixa()
BEGIN
    SELECT c.cod_mov, c.data_hora, c.tipo, c.origem,
           c.cod_produto, p.nome_prod, c.id_servico, s.nm_servico,
           c.cod_agendamento, cl.c_nome AS nome_cliente,
           c.quantidade, c.descricao, c.valor
      FROM caixa c
      LEFT JOIN produto p ON p.cod_produto = c.cod_produto
      LEFT JOIN servicos s ON s.id_servico = c.id_servico
      LEFT JOIN agendamento a ON a.cod_agendamento = c.cod_agendamento
      LEFT JOIN cliente cl ON cl.cod_cliente = a.cod_cliente
     ORDER BY c.data_hora DESC, c.cod_mov DESC;
END$$

CREATE PROCEDURE proc_selc_agendamentos_disponiveis_caixa()
BEGIN
    SELECT a.cod_agendamento, a.cod_cliente, cl.c_nome AS cliente,
           a.id_servico, s.nm_servico AS servico, s.vl_valor AS valor_servico,
           a.data_agendamento, a.horario
      FROM agendamento a
      INNER JOIN cliente cl ON cl.cod_cliente = a.cod_cliente
      INNER JOIN servicos s ON s.id_servico = a.id_servico
     WHERE NOT EXISTS (
         SELECT 1 FROM caixa cx WHERE cx.cod_agendamento = a.cod_agendamento
     )
     ORDER BY a.data_agendamento, a.horario;
END$$

DELIMITER ;
