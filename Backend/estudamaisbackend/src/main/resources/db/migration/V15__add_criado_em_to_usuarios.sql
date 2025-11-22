-- V15: adiciona coluna criado_em na tabela usuarios para compatibilidade com o mapeamento JPA
ALTER TABLE usuarios
  ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP;

-- Popular valores existentes a partir de data_cadastro quando criado_em for NULL
UPDATE usuarios
  SET criado_em = data_cadastro
  WHERE criado_em IS NULL;

-- Definir DEFAULT para novas inserções
ALTER TABLE usuarios
  ALTER COLUMN criado_em SET DEFAULT now();