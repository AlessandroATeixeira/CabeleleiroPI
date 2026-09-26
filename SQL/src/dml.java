/*

create database bd_cabeleireiro;
USE bd_cabeleireiro;


create table cliente ( cod_cliente int primary key auto_increment, c_nome varchar(100) not null, c_telefone char(11) not null );
create table produto( cod_produto int auto_increment primary key, nome_prod varchar(60) not null, valor_prod decimal(10,2) not null );
create table pedido( cod_pedido int auto_increment primary key, data_pedido DATE NOT NULL, cod_cliente int not null );
create table item( cod_item INT AUTO_INCREMENT PRIMARY KEY, cod_pedido INT NOT NULL, cod_produto INT NOT NULL, quantidade INT NOT NULL, valor_total DECIMAL(10,2) NOT NULL, FOREIGN KEY (cod_pedido) REFERENCES pedido(cod_pedido), FOREIGN KEY (cod_produto) REFERENCES produto(cod_produto) );


-- =========================================
-- CLIENTE
-- =========================================

DROP PROCEDURE IF EXISTS proc_ins_cliente;
DELIMITER $$

CREATE PROCEDURE proc_ins_cliente (
    IN p_nome VARCHAR(100),
    IN p_telefone CHAR(11)
)
BEGIN
    INSERT INTO cliente (c_nome, c_telefone)
    VALUES (p_nome, p_telefone);
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_upd_cliente;
DELIMITER $$

CREATE PROCEDURE proc_upd_cliente (
    IN p_cod INT,
    IN p_nome VARCHAR(100),
    IN p_telefone CHAR(11)
)
BEGIN
    UPDATE cliente
    SET c_nome = p_nome,
        c_telefone = p_telefone
    WHERE cod_cliente = p_cod;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_del_cliente;
DELIMITER $$

CREATE PROCEDURE proc_del_cliente (
    IN p_cod INT
)
BEGIN
    DELETE FROM cliente
    WHERE cod_cliente = p_cod;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_selc_cliente;
DELIMITER $$

CREATE PROCEDURE proc_selc_cliente()
BEGIN
    SELECT *
    FROM cliente;
END $$

DELIMITER ;


-- =========================================
-- PRODUTO
-- =========================================

DROP PROCEDURE IF EXISTS proc_ins_produto;
DELIMITER $$

CREATE PROCEDURE proc_ins_produto (
    IN p_cod INT,
    IN p_nome VARCHAR(60),
    IN p_valor DECIMAL(10,2)
)
BEGIN
    INSERT INTO produto (cod_produto, nome_prod, valor_prod)
    VALUES (p_cod, p_nome, p_valor);
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_upd_produto;
DELIMITER $$

CREATE PROCEDURE proc_upd_produto (
    IN p_cod INT,
    IN p_nome VARCHAR(60),
    IN p_valor DECIMAL(10,2)
)
BEGIN
    UPDATE produto
    SET nome_prod = p_nome,
        valor_prod = p_valor
    WHERE cod_produto = p_cod;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_del_produto;
DELIMITER $$

CREATE PROCEDURE proc_del_produto (
    IN p_cod INT
)
BEGIN
    DELETE FROM produto
    WHERE cod_produto = p_cod;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_selc_produtos;
DELIMITER $$

CREATE PROCEDURE proc_selc_produtos()
BEGIN
    SELECT *
    FROM produto;
END $$

DELIMITER ;


-- =========================================
-- PEDIDO
-- =========================================

DROP PROCEDURE IF EXISTS proc_ins_pedido;
DELIMITER $$

CREATE PROCEDURE proc_ins_pedido (
    IN p_data DATE,
    IN p_cliente INT
)
BEGIN
    INSERT INTO pedido (data_pedido, cod_cliente)
    VALUES (p_data, p_cliente);
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_upd_pedido;
DELIMITER $$

CREATE PROCEDURE proc_upd_pedido (
    IN p_cod INT,
    IN p_data DATE,
    IN p_cliente INT
)
BEGIN
    UPDATE pedido
    SET data_pedido = p_data,
        cod_cliente = p_cliente
    WHERE cod_pedido = p_cod;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_del_pedido;
DELIMITER $$

CREATE PROCEDURE proc_del_pedido (
    IN p_cod INT
)
BEGIN
    DELETE FROM pedido
    WHERE cod_pedido = p_cod;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_selc_pedido;
DELIMITER $$

CREATE PROCEDURE proc_selc_pedido()
BEGIN
    SELECT
        pe.cod_pedido,
        pe.data_pedido,
        c.cod_cliente,
        c.c_nome,
        c.c_telefone
    FROM pedido pe
    INNER JOIN cliente c
        ON pe.cod_cliente = c.cod_cliente;
END $$

DELIMITER ;


-- =========================================
-- ITEM
-- =========================================

DROP PROCEDURE IF EXISTS proc_ins_item;
DELIMITER $$

CREATE PROCEDURE proc_ins_item (
    IN p_cod_pedido INT,
    IN p_cod_produto INT,
    IN p_quantidade INT
)
BEGIN
    DECLARE v_valor DECIMAL(10,2);

    SELECT valor_prod
    INTO v_valor
    FROM produto
    WHERE cod_produto = p_cod_produto;

    IF v_valor IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Produto não encontrado';
    END IF;

    IF p_quantidade <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Quantidade deve ser maior que zero';
    END IF;

    INSERT INTO item (
        cod_pedido,
        cod_produto,
        quantidade,
        valor_total
    )
    VALUES (
        p_cod_pedido,
        p_cod_produto,
        p_quantidade,
        v_valor * p_quantidade
    );
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_upd_item;
DELIMITER $$

CREATE PROCEDURE proc_upd_item (
    IN p_cod_item INT,
    IN p_quantidade INT
)
BEGIN
    DECLARE v_valor DECIMAL(10,2);
    DECLARE v_cod_produto INT;

    SELECT cod_produto
    INTO v_cod_produto
    FROM item
    WHERE cod_item = p_cod_item;

    SELECT valor_prod
    INTO v_valor
    FROM produto
    WHERE cod_produto = v_cod_produto;

    IF v_cod_produto IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Item não encontrado';
    END IF;

    IF p_quantidade <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Quantidade deve ser maior que zero';
    END IF;

    UPDATE item
    SET quantidade = p_quantidade,
        valor_total = v_valor * p_quantidade
    WHERE cod_item = p_cod_item;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_del_item;
DELIMITER $$

CREATE PROCEDURE proc_del_item (
    IN p_cod_item INT
)
BEGIN
    DELETE FROM itemproc_selc_produtos
    WHERE cod_item = p_cod_item;
END $$

DELIMITER ;


DROP PROCEDURE IF EXISTS proc_selc_item;
DELIMITER $$

CREATE PROCEDURE proc_selc_item (
    IN p_cod_pedido INT
)
BEGIN
    SELECT
        i.cod_item,
        i.cod_pedido,
        p.cod_produto,
        p.nome_prod,
        p.valor_prod,
        i.quantidade,
        i.valor_total
    FROM item i
    INNER JOIN produto p
        ON i.cod_produto = p.cod_produto
    WHERE i.cod_pedido = p_cod_pedido;
END $$

DELIMITER ;


CALL proc_ins_cliente(
    1,
    'Matheus',
    '11937561773'
);
CALL proc_ins_produto(
    1,
    'Geladeira',
    1000.20
);
CALL proc_ins_pedido(
    '2026-09-25',
    1
);
CALL proc_ins_item(
    1,
    1,
    3
);
CALL proc_selc_cliente();

CALL proc_selc_produtos();

CALL proc_selc_pedido();

CALL proc_selc_item(1);


*/
