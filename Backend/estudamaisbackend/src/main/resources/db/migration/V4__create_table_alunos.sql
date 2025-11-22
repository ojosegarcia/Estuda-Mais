-- V4: cria tabela de alunos (subclasse JOINED -> referencia usuarios.id)
CREATE TABLE alunos (
  id BIGINT PRIMARY KEY,
  escolaridade VARCHAR(255),
  interesse VARCHAR(100),
  CONSTRAINT fk_aluno_usuario FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);