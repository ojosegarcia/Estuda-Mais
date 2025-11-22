-- V8: cria tabela de disponibilidades do professor
CREATE TABLE disponibilidades (
  id BIGSERIAL PRIMARY KEY,
  professor_id BIGINT NOT NULL,
  dia_semana VARCHAR(30) NOT NULL,
  horario_inicio TIME NOT NULL,
  horario_fim TIME NOT NULL,
  ativo BOOLEAN DEFAULT true,
  CONSTRAINT fk_disp_prof FOREIGN KEY (professor_id) REFERENCES professores(id) ON DELETE CASCADE
);