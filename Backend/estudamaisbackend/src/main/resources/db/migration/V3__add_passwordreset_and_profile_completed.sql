-- V3: adiciona tabela password_reset_tokens e coluna profile_completed em aluno (se não existir)

-- 1) password_reset_tokens
CREATE TABLE IF NOT EXISTS password_reset_tokens (
  token VARCHAR(128) PRIMARY KEY,
  usuario_id INTEGER NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  CONSTRAINT fk_prt_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_prt_usuario ON password_reset_tokens(usuario_id);

-- 2) profile_completed em aluno (se já não existir)
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS profile_completed BOOLEAN DEFAULT FALSE;
ALTER TABLE aluno ADD COLUMN IF NOT EXISTS profile_completed_at TIMESTAMP;