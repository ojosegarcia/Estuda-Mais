-- Script para aprovar TODOS os professores existentes no banco
-- Execute este script no pgAdmin ou DBeaver

-- Atualiza todos os professores para aprovado=true
UPDATE usuario 
SET aprovado = true 
WHERE tipo_usuario = 'PROFESSOR' 
  AND (aprovado = false OR aprovado IS NULL);

-- Verifica o resultado
SELECT 
    id_usuario, 
    nome_completo, 
    email,
    aprovado, 
    tipo_usuario 
FROM usuario 
WHERE tipo_usuario = 'PROFESSOR' 
ORDER BY id_usuario;

-- Verifica quantos professores estão aprovados
SELECT 
    COUNT(*) as total_professores,
    SUM(CASE WHEN aprovado = true THEN 1 ELSE 0 END) as aprovados,
    SUM(CASE WHEN aprovado = false OR aprovado IS NULL THEN 1 ELSE 0 END) as nao_aprovados
FROM usuario 
WHERE tipo_usuario = 'PROFESSOR';
