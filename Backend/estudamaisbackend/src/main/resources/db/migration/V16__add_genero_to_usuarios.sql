ALTER TABLE usuarios
  ADD COLUMN IF NOT EXISTS genero VARCHAR(50);

-- Popular valores existentes a partir da coluna 'sexo' quando 'genero' for NULL
UPDATE usuarios
  SET genero = sexo
  WHERE genero IS NULL;

-- Opcional: você pode definir DEFAULT se desejar (não obrigatório)
-- ALTER TABLE usuarios ALTER COLUMN genero SET DEFAULT 'N/A';