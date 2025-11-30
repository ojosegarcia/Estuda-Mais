-- 1. USUARIO
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY, 
    nome_completo VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    telefone VARCHAR(20),
    data_nascimento DATE,
    sexo VARCHAR(20),
    foto_perfil VARCHAR(500),
    data_cadastro TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE,
    tipo_usuario VARCHAR(20)
);

-- 2. ALUNO
CREATE TABLE aluno (
    id_usuario INTEGER PRIMARY KEY, 
    escolaridade VARCHAR(100),
    interesse VARCHAR(50),
    CONSTRAINT fk_aluno_user FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- 3. PROFESSOR
CREATE TABLE professor (
    id_usuario INTEGER PRIMARY KEY,
    sobre TEXT,
    metodologia TEXT,
    valor_hora DECIMAL(10,2),
    foto_certificado VARCHAR(500),
    aprovado BOOLEAN DEFAULT FALSE,
    link_padrao_aula VARCHAR(500),
    usar_link_padrao BOOLEAN,
    CONSTRAINT fk_prof_user FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- 4. MATERIA
CREATE TABLE materia (
    id_materia SERIAL PRIMARY KEY,
    nome_materia VARCHAR(100),
    descricao_materia TEXT,
    icone_materia VARCHAR(50)
);

-- 5. PROFESSOR_MATERIA
CREATE TABLE professor_materia (
    id_professor INTEGER NOT NULL,
    id_materia INTEGER NOT NULL,
    PRIMARY KEY (id_professor, id_materia),
    CONSTRAINT fk_pm_prof FOREIGN KEY (id_professor) REFERENCES professor(id_usuario),
    CONSTRAINT fk_pm_mat FOREIGN KEY (id_materia) REFERENCES materia(id_materia)
);

-- 6. DISPONIBILIDADE
CREATE TABLE disponibilidade (
    id_disponibilidade SERIAL PRIMARY KEY,
    id_professor INTEGER NOT NULL,
    dia_semana VARCHAR(20),
    horario_inicio TIME,
    horario_fim TIME,
    ativo BOOLEAN,
    CONSTRAINT fk_disp_prof FOREIGN KEY (id_professor) REFERENCES professor(id_usuario)
);

-- 7. AULA (AQUI ESTAVA A DÚVIDA)
CREATE TABLE aula (
    id_aula SERIAL PRIMARY KEY, -- Mudamos para SERIAL (Número Sequencial)
    id_professor INTEGER NOT NULL,
    id_aluno INTEGER NOT NULL,
    id_materia INTEGER NOT NULL,
    data_aula DATE,
    horario_inicio TIME,
    horario_fim TIME,
    status_aula VARCHAR(20),
    link_reuniao VARCHAR(500),
    valor_aula DECIMAL(10,2),
    data_criacao TIMESTAMP,
    removido_pelo_aluno BOOLEAN DEFAULT FALSE,
    removido_pelo_professor BOOLEAN DEFAULT FALSE,
    observacoes TEXT,
    tipo_aula VARCHAR(20),
    CONSTRAINT fk_aula_prof FOREIGN KEY (id_professor) REFERENCES professor(id_usuario),
    CONSTRAINT fk_aula_aluno FOREIGN KEY (id_aluno) REFERENCES aluno(id_usuario),
    CONSTRAINT fk_aula_mat FOREIGN KEY (id_materia) REFERENCES materia(id_materia)
);

-- 8. FEEDBACK
CREATE TABLE feedback (
    id_feedback SERIAL PRIMARY KEY,
    id_aula INTEGER NOT NULL UNIQUE, -- Referencia o ID numérico da aula
    id_aluno_autor INTEGER,
    id_professor_alvo INTEGER,
    nota INTEGER,
    comentario_privado TEXT,
    comentario_publico TEXT,
    data_feedback TIMESTAMP,
    recomenda BOOLEAN,
    CONSTRAINT fk_feed_aula FOREIGN KEY (id_aula) REFERENCES aula(id_aula)
);

-- 9. EXPERIENCIA e CONQUISTA e FAVORITOS (Mantém padrão Serial/Integer)
CREATE TABLE experiencia_profissional (
    id_experiencia SERIAL PRIMARY KEY,
    id_professor INTEGER NOT NULL,
    cargo VARCHAR(255),
    empresa_instituicao VARCHAR(255),
    periodo VARCHAR(100),
    descricao TEXT,
    CONSTRAINT fk_exp_prof FOREIGN KEY (id_professor) REFERENCES professor(id_usuario)
);

CREATE TABLE conquista (
    id_conquista SERIAL PRIMARY KEY,
    id_professor INTEGER NOT NULL,
    titulo_conquista VARCHAR(255),
    ano INTEGER,
    descricao TEXT,
    CONSTRAINT fk_conq_prof FOREIGN KEY (id_professor) REFERENCES professor(id_usuario)
);

CREATE TABLE favoritos (
    id_aluno INTEGER NOT NULL,
    id_professor INTEGER NOT NULL,
    data_favoritado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_aluno, id_professor),
    CONSTRAINT fk_fav_aluno FOREIGN KEY (id_aluno) REFERENCES aluno(id_usuario),
    CONSTRAINT fk_fav_prof FOREIGN KEY (id_professor) REFERENCES professor(id_usuario)
);