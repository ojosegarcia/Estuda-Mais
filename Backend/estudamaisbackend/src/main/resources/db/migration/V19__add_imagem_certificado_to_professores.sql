
ALTER TABLE professores
  ADD COLUMN IF NOT EXISTS imagem_certificado VARCHAR(512);

UPDATE professores
  SET imagem_certificado = foto_certificado
  WHERE imagem_certificado IS NULL
    AND foto_certificado IS NOT NULL;

-- (Opcional) definir DEFAULT se desejar
-- ALTER TABLE professores ALTER COLUMN imagem_certificado SET DEFAULT '';