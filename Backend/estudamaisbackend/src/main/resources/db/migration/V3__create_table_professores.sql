-- V3: cria tabela de professores (subclasse JOINED -> referencia usuarios.id)
CREATE TABLE professores (
  id BIGINT PRIMARY KEY,
  sobre TEXT,
  metodologia TEXT,
  valor_hora NUMERIC(10,2),
  foto_certificado VARCHAR(512),
  aprovado BOOLEAN DEFAULT false,
  CONSTRAINT fk_professor_usuario FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);