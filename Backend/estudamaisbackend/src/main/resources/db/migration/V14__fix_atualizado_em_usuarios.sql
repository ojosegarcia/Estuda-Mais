-- V14: corrige criação/população/DEFAULT/trigger para a coluna atualizado_em na tabela usuarios
-- usa a coluna correta de criação (data_cadastro) presente nas suas migrations originais.

ALTER TABLE usuarios
  ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP;

-- Popular valores existentes com data_cadastro quando atualizado_em for NULL
UPDATE usuarios
  SET atualizado_em = data_cadastro
  WHERE atualizado_em IS NULL;

-- Definir DEFAULT para novas inserções
ALTER TABLE usuarios
  ALTER COLUMN atualizado_em SET DEFAULT now();

-- Função/trigger para atualizar atualizado_em automaticamente em UPDATE
CREATE OR REPLACE FUNCTION set_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.atualizado_em = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_set_updated_at_usuarios'
  ) THEN
    CREATE TRIGGER trigger_set_updated_at_usuarios
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at_column();
  END IF;
END;
$$;