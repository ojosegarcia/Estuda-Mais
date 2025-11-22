-- V11: índices úteis
CREATE INDEX idx_aulas_professor_id ON aulas(id_professor);
CREATE INDEX idx_aulas_aluno_id ON aulas(id_aluno);
CREATE INDEX idx_professor_materias_materia_id ON professor_materias(materia_id);
CREATE INDEX idx_materias_nome ON materias(nome);
CREATE INDEX idx_usuarios_email ON usuarios(email);