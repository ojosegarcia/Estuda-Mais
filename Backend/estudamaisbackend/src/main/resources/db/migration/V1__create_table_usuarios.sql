-- V1: cria tabela de usuários (tabela pai para herança JOINED)
CREATE TABLE usuarios (
  id BIGSERIAL PRIMARY KEY,
  nome_completo VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL,
  telefone VARCHAR(50),
  data_nascimento DATE,
  sexo VARCHAR(50),
  foto_perfil VARCHAR(512),
  data_cadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  ativo BOOLEAN DEFAULT true,
  tipo_usuario VARCHAR(50) -- discriminator: 'ALUNO' ou 'PROFESSOR'
);