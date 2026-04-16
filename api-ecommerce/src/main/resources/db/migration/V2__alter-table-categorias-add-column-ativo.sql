ALTER TABLE categorias add ativo tinyint;

update categorias set ativo = 1;

alter table categorias modify column ativo tinyint not null;