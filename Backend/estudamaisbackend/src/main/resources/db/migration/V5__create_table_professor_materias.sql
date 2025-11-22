-- V5: tabela de relacionamento many-to-many entre professores e materias
CREATE TABLE professor_materias (
  professor_id BIGINT NOT NULL,
  materia_id BIGINT NOT NULL,
  PRIMARY KEY (professor_id, materia_id),
  CONSTRAINT fk_pm_prof FOREIGN KEY (professor_id) REFERENCES professores(id) ON DELETE CASCADE,
  CONSTRAINT fk_pm_mat FOREIGN KEY (materia_id) REFERENCES materias(id) ON DELETE CASCADE
);