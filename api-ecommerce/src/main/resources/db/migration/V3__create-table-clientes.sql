CREATE TABLE clientes (
                          id bigint AUTO_INCREMENT primary key,

                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          cpf CHAR(11) NOT NULL UNIQUE,

                          telefone VARCHAR(20),

                          ativo TINYINT(1) NOT NULL DEFAULT 1

);