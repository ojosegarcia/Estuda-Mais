import { Usuario } from './usuarioModel';

export interface Aluno extends Usuario {
  escolaridade?: string;
  interesse?: string; // Objetivo do aluno: aprender algo novo, reforçar conhecimento, preparar para conquista
}
