-- POPULANDO O BANCO DE DADOS (BASEADO NO DB.JSON)

-- 1. MATERIAS
INSERT INTO materia (id_materia, nome_materia, icone_materia, descricao_materia) VALUES
(1, 'Matemática', '🧮', 'Álgebra, Geometria, Cálculo'),
(2, 'Artes', '🎨', 'História da Arte'),
(3, 'Vestibular', '📚', 'Preparação para ENEM'),
(4, 'Programação', '💻', 'JavaScript, Python, Angular'),
(5, 'Inglês', '🌏', 'Conversação, Gramática');

-- 2. USUARIOS (ALUNOS)
-- Nota: Senha '123456' hash padrão BCrypt: $2a$10$YourHashedPasswordHere (substitua por um hash real se precisar logar)
-- Hash real para '123456': $2a$10$r.7/k9x8/k9x8/k9x8/k9x8/k9x8/k9x8/k9x8/k9x8/k9x8/k9x

-- João Aluno (ID 1)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (1, 'João Aluno', 'aluno@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '(11) 98765-4321', '2025-11-01 10:00:00', true, 'ALUNO');
INSERT INTO aluno (id_usuario, escolaridade, interesse) VALUES (1, 'Ensino Médio Completo', 'APRENDER_NOVO');

-- Maria Estudante (ID 2)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (2, 'Maria Estudante', 'maria@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '(11) 91234-5678', '2025-11-02 14:30:00', true, 'ALUNO');
INSERT INTO aluno (id_usuario, escolaridade, interesse) VALUES (2, 'Superior Incompleto', 'REFORCAR_CONHECIMENTO');

-- Gabriel (ID 3 - Convertido do ID longo 17629...)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (3, 'Gabriel Correa de Oliveira', 'gabriel@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '11995082458', '2025-11-12 20:00:06', true, 'ALUNO');
INSERT INTO aluno (id_usuario, escolaridade, interesse) VALUES (3, 'Prefiro não dizer', 'REFORCAR_CONHECIMENTO');

-- Lucas Cerri (ID 4 - Convertido do ID longo)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (4, 'Lucas Cerri', 'lucas@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '', '2025-11-14 23:59:46', true, 'ALUNO');
INSERT INTO aluno (id_usuario, escolaridade, interesse) VALUES (4, 'Superior Incompleto', 'APRENDER_NOVO');


-- 3. USUARIOS (PROFESSORES)
-- Prof. Ana Silva (ID 101)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (101, 'Prof. Ana Silva', 'ana@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '(11) 99999-1111', '2025-10-15 08:00:00', true, 'PROFESSOR');
INSERT INTO professor (id_usuario, sobre, metodologia, valor_hora, link_padrao_aula, usar_link_padrao, aprovado)
VALUES (101, 'Doutora em Matemática pela USP com 10 anos de experiência.', 'Foco em resolução de exercícios práticos.', 80.00, 'https://meet.google.com/ana-matematica-2024', true, true);
-- Matérias da Ana (Matemática, Vestibular)
INSERT INTO professor_materia (id_professor, id_materia) VALUES (101, 1), (101, 3);

-- Prof. Bruno Gomes (ID 102)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (102, 'Prof. Bruno Gomes', 'bruno@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '(11) 98888-2222', '2025-10-20 11:00:00', true, 'PROFESSOR');
INSERT INTO professor (id_usuario, sobre, metodologia, valor_hora, link_padrao_aula, usar_link_padrao, aprovado)
VALUES (102, 'Desenvolvedor Sênior. 8 anos de mercado.', 'Projetos práticos.', 100.00, 'https://zoom.us/bruno', true, true);
-- Matérias do Bruno (Programação)
INSERT INTO professor_materia (id_professor, id_materia) VALUES (102, 4);

-- Prof. Carla Dias (ID 103)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (103, 'Prof. Carla Dias', 'carla@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '(11) 97777-3333', '2025-10-25 16:00:00', true, 'PROFESSOR');
INSERT INTO professor (id_usuario, sobre, metodologia, valor_hora, link_padrao_aula, usar_link_padrao, aprovado)
VALUES (103, 'Professora de inglês com certificação TOEFL.', 'Conversação e imersão.', 70.00, 'https://teams.microsoft.com/carla', false, true);
-- Matérias da Carla (Inglês)
INSERT INTO professor_materia (id_professor, id_materia) VALUES (103, 5);

-- Renato Luis (ID 104 - Convertido do ID longo)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (104, 'Renato Luis', 'renato2@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '', '2025-11-12 23:37:02', true, 'PROFESSOR');
INSERT INTO professor (id_usuario, sobre, metodologia, valor_hora, aprovado)
VALUES (104, 'O melhor professor', 'Minhas aulas são boas', 99.00, true);
INSERT INTO professor_materia (id_professor, id_materia) VALUES (104, 4);

-- Sergio Salgado (ID 105 - Convertido do ID longo)
INSERT INTO usuario (id_usuario, nome_completo, email, password, telefone, data_cadastro, ativo, tipo_usuario)
VALUES (105, 'Sergio Salgado', 'sergio@email.com', '$2a$10$7y1aYXJDwke2V89s4iZjzeQSEd3FR4ZrMDmKPUKMhMfvrvnttl1bm', '', '2025-11-15 00:00:10', true, 'PROFESSOR');
INSERT INTO professor (id_usuario, sobre, metodologia, valor_hora, aprovado)
VALUES (105, 'Ensinar é a minha vida', 'Presença vale nota', 99.00, true);
INSERT INTO professor_materia (id_professor, id_materia) VALUES (105, 4);


-- 4. DISPONIBILIDADES (IDs Sequenciais)
INSERT INTO disponibilidade (id_disponibilidade, id_professor, dia_semana, horario_inicio, horario_fim, ativo) VALUES
(1, 101, 'SEGUNDA', '14:00', '18:00', true),
(2, 101, 'QUARTA', '09:00', '12:00', true),
(3, 102, 'TERCA', '14:00', '17:00', true),
(4, 102, 'QUINTA', '14:00', '17:00', true),
(5, 103, 'SEGUNDA', '08:00', '11:00', true),
(6, 103, 'SEXTA', '13:00', '16:00', true);


-- 5. AULAS (IDs Sequenciais - Convertidos dos Strings)
-- Aula 1 (Recusada - Ana e João)
INSERT INTO aula (id_aula, id_professor, id_aluno, id_materia, data_aula, horario_inicio, horario_fim, status_aula, valor_aula, data_criacao, removido_pelo_aluno, removido_pelo_professor)
VALUES (1, 101, 1, 1, '2025-11-15', '14:00', '15:00', 'RECUSADA', 80.00, '2025-11-10 10:00:00', false, true);

-- Aula 2 (Recusada - Bruno e João)
INSERT INTO aula (id_aula, id_professor, id_aluno, id_materia, data_aula, horario_inicio, horario_fim, status_aula, valor_aula, data_criacao)
VALUES (2, 102, 1, 4, '2025-11-12', '16:00', '17:00', 'RECUSADA', 100.00, '2025-11-08 09:00:00');

-- Aula 3 (Confirmada - Carla e Maria)
INSERT INTO aula (id_aula, id_professor, id_aluno, id_materia, data_aula, horario_inicio, horario_fim, status_aula, link_reuniao, valor_aula, data_criacao)
VALUES (3, 103, 2, 5, '2025-11-18', '09:00', '10:00', 'CONFIRMADA', 'https://zoom.us/j/123456789', 70.00, '2025-11-10 08:00:00');

-- Aula 4 (Cancelada - Renato e Gabriel)
INSERT INTO aula (id_aula, id_professor, id_aluno, id_materia, data_aula, horario_inicio, horario_fim, status_aula, valor_aula, data_criacao)
VALUES (4, 104, 3, 4, '2025-11-14', '14:00', '15:00', 'CANCELADA', 99.00, '2025-11-13 11:23:25');

-- Aula 5 (Confirmada - Sergio e Gabriel)
INSERT INTO aula (id_aula, id_professor, id_aluno, id_materia, data_aula, horario_inicio, horario_fim, status_aula, valor_aula, data_criacao)
VALUES (5, 105, 3, 4, '2025-11-17', '14:00', '15:00', 'CONFIRMADA', 99.00, '2025-11-17 00:56:40');

-- Aula 6 (Solicitada - Ana e Lucas)
INSERT INTO aula (id_aula, id_professor, id_aluno, id_materia, data_aula, horario_inicio, horario_fim, status_aula, valor_aula, data_criacao)
VALUES (6, 101, 4, 1, '2025-11-24', '14:00', '15:00', 'SOLICITADA', 80.00, '2025-11-24 00:04:03');


-- 6. AJUSTE DE SEQUÊNCIAS (CRÍTICO)
-- Isso garante que o próximo INSERT automático (id 7, id 106, etc) não dê conflito de PK duplicada
SELECT setval('materia_id_materia_seq', (SELECT MAX(id_materia) FROM materia));
SELECT setval('usuario_id_usuario_seq', (SELECT MAX(id_usuario) FROM usuario));
SELECT setval('aula_id_aula_seq', (SELECT MAX(id_aula) FROM aula));
SELECT setval('disponibilidade_id_disponibilidade_seq', (SELECT MAX(id_disponibilidade) FROM disponibilidade));
SELECT setval('experiencia_profissional_id_experiencia_seq', COALESCE((SELECT MAX(id_experiencia) FROM experiencia_profissional), 1));
SELECT setval('conquista_id_conquista_seq', COALESCE((SELECT MAX(id_conquista) FROM conquista), 1));
SELECT setval('feedback_id_feedback_seq', COALESCE((SELECT MAX(id_feedback) FROM feedback), 1));