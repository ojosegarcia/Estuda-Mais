-- V7: cria tabela de feedbacks (1 feedback por aula)
CREATE TABLE feedbacks (
  id BIGSERIAL PRIMARY KEY,
  id_aula BIGINT NOT NULL UNIQUE,
  id_aluno BIGINT,
  id_professor BIGINT,
  nota INTEGER,
  comentario_privado TEXT,
  comentario_publico TEXT,
  data_feedback TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  recomenda BOOLEAN DEFAULT true,
  CONSTRAINT fk_fb_aula FOREIGN KEY (id_aula) REFERENCES aulas(id) ON DELETE CASCADE,
  CONSTRAINT fk_fb_aluno FOREIGN KEY (id_aluno) REFERENCES alunos(id) ON DELETE SET NULL,
  CONSTRAINT fk_fb_prof FOREIGN KEY (id_professor) REFERENCES professores(id) ON DELETE SET NULL
);