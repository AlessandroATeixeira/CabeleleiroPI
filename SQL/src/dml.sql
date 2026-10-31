
DROP DATABASE IF EXISTS bd_cabeleireiro;
CREATE DATABASE bd_cabeleireiro
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bd_cabeleireiro;

-- =========================================================
        -- TABELAS
-- Os codigos sao informados manualmente; nao usam AUTO_INCREMENT.
-- =========================================================

CREATE TABLE cliente (
        cod_cliente INT PRIMARY KEY,
        c_nome VARCHAR(100) NOT NULL,
        c_telefone CHAR(11) NOT NULL
);

CREATE TABLE produto (
        cod_produto INT PRIMARY KEY,
        nome_prod VARCHAR(60) NOT NULL,
        valor_prod DECIMAL(10,2) NOT NULL,
        estoque INT NOT NULL DEFAULT 0,
        CONSTRAINT chk_produto_valor CHECK (valor_prod >= 0),
        CONSTRAINT chk_produto_estoque CHECK (estoque >= 0)
);

CREATE TABLE pedido (
        cod_pedido INT PRIMARY KEY,
        data_pedido DATE NOT NULL,
        cod_cliente INT NOT NULL,
        CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cod_cliente) REFERENCES cliente(cod_cliente)
        );

CREATE TABLE item (
        cod_item INT PRIMARY KEY,
        cod_pedido INT NOT NULL,
        cod_produto INT NOT NULL,
        quantidade INT NOT NULL,
        valor_total DECIMAL(10,2) NOT NULL,
CONSTRAINT chk_item_quantidade CHECK (quantidade > 0),
CONSTRAINT chk_item_total CHECK (valor_total >= 0),
CONSTRAINT fk_item_pedido
FOREIGN KEY (cod_pedido) REFERENCES pedido(cod_pedido),
CONSTRAINT fk_item_produto
FOREIGN KEY (cod_produto) REFERENCES produto(cod_produto)
        );

CREATE TABLE agendamento (
        cod_agendamento INT PRIMARY KEY,
        servico VARCHAR(100) NOT NULL,
        data_agendamento DATE NOT NULL,
        horario TIME NOT NULL
);

        -- =========================================================
        -- PROCEDURES: CLIENTE
-- =========================================================

DELIMITER $$

CREATE PROCEDURE proc_ins_cliente(
        IN p_cod INT,
        IN p_nome VARCHAR(100),
IN p_telefone CHAR(11)
)
BEGIN
IF p_cod IS NULL OR p_cod <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O codigo do cliente deve ser maior que zero';
END IF;

IF p_nome IS NULL OR TRIM(p_nome) = '' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O nome do cliente e obrigatorio';
END IF;

IF p_telefone IS NULL OR CHAR_LENGTH(p_telefone) <> 11
OR p_telefone NOT REGEXP '^[0-9]{11}$' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O telefone deve conter 11 digitos, incluindo DDD';
END IF;

INSERT INTO cliente (cod_cliente, c_nome, c_telefone)
VALUES (p_cod, TRIM(p_nome), p_telefone);
END$$

CREATE PROCEDURE proc_upd_cliente(
        IN p_cod INT,
        IN p_nome VARCHAR(100),
IN p_telefone CHAR(11)
)
BEGIN
IF NOT EXISTS (SELECT 1 FROM cliente WHERE cod_cliente = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Cliente nao encontrado';
END IF;

IF p_nome IS NULL OR TRIM(p_nome) = '' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O nome do cliente e obrigatorio';
END IF;

IF p_telefone IS NULL OR CHAR_LENGTH(p_telefone) <> 11
OR p_telefone NOT REGEXP '^[0-9]{11}$' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O telefone deve conter 11 digitos, incluindo DDD';
END IF;

UPDATE cliente
        SET c_nome = TRIM(p_nome), c_telefone = p_telefone
WHERE cod_cliente = p_cod;
END$$

CREATE PROCEDURE proc_del_cliente(IN p_cod INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM cliente WHERE cod_cliente = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Cliente nao encontrado';
END IF;

IF EXISTS (SELECT 1 FROM pedido WHERE cod_cliente = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Cliente possui pedidos associados';
END IF;

DELETE FROM cliente WHERE cod_cliente = p_cod;
END$$

CREATE PROCEDURE proc_selc_clientes()
BEGIN
        SELECT cod_cliente, c_nome, c_telefone
FROM cliente
ORDER BY cod_cliente;
END$$

-- =========================================================
        -- PROCEDURES: PRODUTO
-- =========================================================

CREATE PROCEDURE proc_ins_produto(
        IN p_cod INT,
        IN p_nome VARCHAR(60),
IN p_valor DECIMAL(10,2),
IN p_estoque INT
)
BEGIN
IF p_cod IS NULL OR p_cod <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O codigo do produto deve ser maior que zero';
END IF;

IF p_nome IS NULL OR TRIM(p_nome) = '' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O nome do produto e obrigatorio';
END IF;

IF p_valor IS NULL OR p_valor < 0 OR p_estoque IS NULL OR p_estoque < 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Valor e estoque devem ser zero ou positivos';
END IF;

INSERT INTO produto (cod_produto, nome_prod, valor_prod, estoque)
VALUES (p_cod, TRIM(p_nome), p_valor, p_estoque);
END$$

CREATE PROCEDURE proc_upd_produto(
        IN p_cod INT,
        IN p_nome VARCHAR(60),
IN p_valor DECIMAL(10,2),
IN p_estoque INT
)
BEGIN
IF NOT EXISTS (SELECT 1 FROM produto WHERE cod_produto = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Produto nao encontrado';
END IF;

IF p_nome IS NULL OR TRIM(p_nome) = '' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O nome do produto e obrigatorio';
END IF;

IF p_valor IS NULL OR p_valor < 0 OR p_estoque IS NULL OR p_estoque < 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Valor e estoque devem ser zero ou positivos';
END IF;

UPDATE produto
        SET nome_prod = TRIM(p_nome), valor_prod = p_valor, estoque = p_estoque
WHERE cod_produto = p_cod;
END$$

CREATE PROCEDURE proc_del_produto(IN p_cod INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM produto WHERE cod_produto = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Produto nao encontrado';
END IF;

IF EXISTS (SELECT 1 FROM item WHERE cod_produto = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Produto possui itens associados';
END IF;

DELETE FROM produto WHERE cod_produto = p_cod;
END$$

CREATE PROCEDURE proc_selc_produtos()
BEGIN
        SELECT cod_produto, nome_prod, valor_prod, estoque
FROM produto
ORDER BY cod_produto;
END$$

-- =========================================================
        -- PROCEDURES: PEDIDO
-- =========================================================

CREATE PROCEDURE proc_ins_pedido(
        IN p_cod INT,
        IN p_data DATE,
        IN p_cliente INT
)
BEGIN
IF p_cod IS NULL OR p_cod <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O codigo do pedido deve ser maior que zero';
END IF;

IF NOT EXISTS (SELECT 1 FROM cliente WHERE cod_cliente = p_cliente) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Cliente nao encontrado';
END IF;

INSERT INTO pedido (cod_pedido, data_pedido, cod_cliente)
VALUES (p_cod, p_data, p_cliente);
END$$

CREATE PROCEDURE proc_upd_pedido(
        IN p_cod INT,
        IN p_data DATE,
        IN p_cliente INT
)
BEGIN
IF NOT EXISTS (SELECT 1 FROM pedido WHERE cod_pedido = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Pedido nao encontrado';
END IF;

IF NOT EXISTS (SELECT 1 FROM cliente WHERE cod_cliente = p_cliente) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Cliente nao encontrado';
END IF;

UPDATE pedido
        SET data_pedido = p_data, cod_cliente = p_cliente
WHERE cod_pedido = p_cod;
END$$

CREATE PROCEDURE proc_del_pedido(IN p_cod INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM pedido WHERE cod_pedido = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Pedido nao encontrado';
END IF;

IF EXISTS (SELECT 1 FROM item WHERE cod_pedido = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Pedido possui itens associados';
END IF;

DELETE FROM pedido WHERE cod_pedido = p_cod;
END$$

CREATE PROCEDURE proc_selc_pedido()
BEGIN
SELECT pe.cod_pedido, pe.data_pedido,
c.cod_cliente, c.c_nome, c.c_telefone
FROM pedido pe
INNER JOIN cliente c ON c.cod_cliente = pe.cod_cliente
ORDER BY pe.cod_pedido;
END$$

-- =========================================================
        -- PROCEDURES: ITEM
-- =========================================================

CREATE PROCEDURE proc_ins_item(
        IN p_cod_item INT,
        IN p_cod_pedido INT,
        IN p_cod_produto INT,
        IN p_quantidade INT
)
BEGIN
DECLARE v_valor DECIMAL(10,2);

IF p_cod_item IS NULL OR p_cod_item <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O codigo do item deve ser maior que zero';
END IF;

IF p_quantidade IS NULL OR p_quantidade <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'A quantidade deve ser maior que zero';
END IF;

SELECT valor_prod INTO v_valor
FROM produto WHERE cod_produto = p_cod_produto;

IF v_valor IS NULL THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Produto nao encontrado';
END IF;

IF NOT EXISTS (SELECT 1 FROM pedido WHERE cod_pedido = p_cod_pedido) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Pedido nao encontrado';
END IF;

INSERT INTO item (cod_item, cod_pedido, cod_produto, quantidade, valor_total)
VALUES (p_cod_item, p_cod_pedido, p_cod_produto, p_quantidade, v_valor * p_quantidade);
END$$

CREATE PROCEDURE proc_upd_item(
        IN p_cod_item INT,
        IN p_quantidade INT
)
BEGIN
DECLARE v_valor DECIMAL(10,2);

IF p_quantidade IS NULL OR p_quantidade <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'A quantidade deve ser maior que zero';
END IF;

IF NOT EXISTS (SELECT 1 FROM item WHERE cod_item = p_cod_item) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Item nao encontrado';
END IF;

SELECT p.valor_prod INTO v_valor
FROM produto p
INNER JOIN item i ON i.cod_produto = p.cod_produto
WHERE i.cod_item = p_cod_item;

UPDATE item
        SET quantidade = p_quantidade, valor_total = v_valor * p_quantidade
WHERE cod_item = p_cod_item;
END$$

CREATE PROCEDURE proc_del_item(IN p_cod_item INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM item WHERE cod_item = p_cod_item) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Item nao encontrado';
END IF;

DELETE FROM item WHERE cod_item = p_cod_item;
END$$

CREATE PROCEDURE proc_selc_item(IN p_cod_pedido INT)
BEGIN
SELECT i.cod_item, i.cod_pedido, p.cod_produto, p.nome_prod,
p.valor_prod, i.quantidade, i.valor_total
FROM item i
INNER JOIN produto p ON p.cod_produto = i.cod_produto
WHERE i.cod_pedido = p_cod_pedido
ORDER BY i.cod_item;
END$$

-- =========================================================
        -- PROCEDURES: AGENDAMENTO
-- =========================================================

CREATE PROCEDURE proc_ins_agendamento(
        IN p_cod INT,
        IN p_servico VARCHAR(100),
IN p_data DATE,
IN p_horario TIME
)
BEGIN
IF p_cod IS NULL OR p_cod <= 0 THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O codigo do agendamento deve ser maior que zero';
END IF;

IF p_servico IS NULL OR TRIM(p_servico) = '' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O servico e obrigatorio';
END IF;

IF p_data IS NULL OR p_horario IS NULL THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Data e horario sao obrigatorios';
END IF;

INSERT INTO agendamento (cod_agendamento, servico, data_agendamento, horario)
VALUES (p_cod, TRIM(p_servico), p_data, p_horario);
END$$

CREATE PROCEDURE proc_selc_agendamentos()
BEGIN
        SELECT cod_agendamento, servico, data_agendamento, horario
FROM agendamento
ORDER BY data_agendamento, horario;
END$$

CREATE PROCEDURE proc_upd_agendamento(
        IN p_cod INT,
        IN p_servico VARCHAR(100),
IN p_data DATE,
IN p_horario TIME
)
BEGIN
IF NOT EXISTS (SELECT 1 FROM agendamento WHERE cod_agendamento = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Agendamento nao encontrado';
END IF;

IF p_servico IS NULL OR TRIM(p_servico) = '' THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'O servico e obrigatorio';
END IF;

IF p_data IS NULL OR p_horario IS NULL THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Data e horario sao obrigatorios';
END IF;

UPDATE agendamento
        SET servico = TRIM(p_servico), data_agendamento = p_data, horario = p_horario
WHERE cod_agendamento = p_cod;
END$$

CREATE PROCEDURE proc_del_agendamento(IN p_cod INT)
BEGIN
IF NOT EXISTS (SELECT 1 FROM agendamento WHERE cod_agendamento = p_cod) THEN
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Agendamento nao encontrado';
END IF;

DELETE FROM agendamento WHERE cod_agendamento = p_cod;
END$$

        DELIMITER ;
