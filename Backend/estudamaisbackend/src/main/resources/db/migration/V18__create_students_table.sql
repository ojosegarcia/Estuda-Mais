-- V18: cria tabela students para compatibilidade com a entidade JPA que usa 'students'
CREATE TABLE IF NOT EXISTS students (
  id BIGINT PRIMARY KEY,
  education_level VARCHAR(255),
  interesse VARCHAR(100),
  CONSTRAINT fk_students_usuario FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);