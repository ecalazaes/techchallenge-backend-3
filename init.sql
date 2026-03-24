-- O Docker já cria o banco definido na variável POSTGRES_DB (scheduling_db).
-- Este script garante a criação dos bancos adicionais necessários para os outros microsserviços.

CREATE DATABASE history_db;
CREATE DATABASE notification_db;
