
ALTER TABLE usuarios
  ADD COLUMN IF NOT EXISTS imagem_perfil VARCHAR(512);

UPDATE usuarios
  SET imagem_perfil = foto_perfil
  WHERE imagem_perfil IS NULL
    AND foto_perfil IS NOT NULL;

-- (Opcional) definir default se quiser
-- ALTER TABLE usuarios ALTER COLUMN imagem_perfil SET DEFAULT '';