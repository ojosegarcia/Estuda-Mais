-- V10: cria tabela de conquistas do professor
CREATE TABLE conquistas (
  id BIGSERIAL PRIMARY KEY,
  professor_id BIGINT NOT NULL,
  titulo_conquista VARCHAR(255) NOT NULL,
  ano INTEGER,
  descricao TEXT,
  CONSTRAINT fk_conq_prof FOREIGN KEY (professor_id) REFERENCES professores(id) ON DELETE CASCADE
);