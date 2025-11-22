-- V2: cria tabela de matérias
CREATE TABLE materias (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  icone VARCHAR(64)
);