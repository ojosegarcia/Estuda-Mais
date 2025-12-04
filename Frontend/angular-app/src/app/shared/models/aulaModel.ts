import { Aluno } from './alunoModel';
import { Professor } from './professorModel';
import { Materia } from './materiaModel';
import { Feedback } from './feedbackModel';

export type StatusAula = 
  | 'SOLICITADA' 
  | 'CONFIRMADA' 
  | 'RECUSADA' 
  | 'REALIZADA' 
  | 'CANCELADA'; 

export interface Aula {
  id: number;
  idProfessor: number;
  idAluno: number;
  idMateria: number;
  
  professor?: Professor;
  aluno?: Aluno;
  materia?: Materia;
  feedback?: Feedback; // ← NOVO
  
  dataAula: string; 
  horarioInicio: string;
  horarioFim: string;
  
  statusAula: StatusAula; 
  
  linkReuniao?: string;
  valorAula: number;
  dataCriacao: string;
  
  removidoPeloAluno?: boolean;
  removidoPeloProfessor?: boolean;
}