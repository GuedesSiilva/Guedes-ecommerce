ALTER TABLE clientes
    ADD CONSTRAINT chk_cep
        CHECK(cep REGEXP '^[0-9]{8}$');

ALTER TABLE clientes
    ADD CONSTRAINT chk_uf
        CHECK(uf REGEXP '^[A-Z]{2}$');