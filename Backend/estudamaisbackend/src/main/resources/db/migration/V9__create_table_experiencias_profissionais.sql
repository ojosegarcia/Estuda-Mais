-- V9: cria tabela de experiencias profissionais do professor
CREATE TABLE experiencias_profissionais (
  id BIGSERIAL PRIMARY KEY,
  professor_id BIGINT NOT NULL,
  cargo VARCHAR(255) NOT NULL,
  instituicao VARCHAR(255) NOT NULL,
  periodo VARCHAR(255) NOT NULL,
  descricao TEXT,
  CONSTRAINT fk_exp_prof FOREIGN KEY (professor_id) REFERENCES professores(id) ON DELETE CASCADE
);