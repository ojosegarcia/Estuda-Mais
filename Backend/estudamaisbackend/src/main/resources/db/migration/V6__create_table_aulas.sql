-- V6: cria tabela de aulas (class sessions)
CREATE TABLE aulas (
  id BIGSERIAL PRIMARY KEY,
  id_professor BIGINT NOT NULL,
  id_aluno BIGINT NOT NULL,
  id_materia BIGINT NOT NULL,
  data_aula DATE NOT NULL,          -- yyyy-MM-dd
  horario_inicio TIME NOT NULL,     -- HH:mm
  horario_fim TIME NOT NULL,        -- HH:mm
  status_aula VARCHAR(50) NOT NULL, -- SOLICITADA, CONFIRMADA, RECUSADA, REALIZADA, CANCELADA
  link_reuniao VARCHAR(1024),
  valor_aula NUMERIC(10,2),
  data_criacao TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  CONSTRAINT fk_aula_prof FOREIGN KEY (id_professor) REFERENCES professores(id) ON DELETE CASCADE,
  CONSTRAINT fk_aula_aluno FOREIGN KEY (id_aluno) REFERENCES alunos(id) ON DELETE CASCADE,
  CONSTRAINT fk_aula_mat FOREIGN KEY (id_materia) REFERENCES materias(id) ON DELETE RESTRICT
);