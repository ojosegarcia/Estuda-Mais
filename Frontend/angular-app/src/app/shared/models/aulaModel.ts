import { Aluno } from './alunoModel';
import { Professor } from './professorModel';
import { Materia } from './materiaModel';

export interface Aula {
  id: number;
  idProfessor: number;
  idAluno: number;
  idMateria: number;
  professor?: Professor;
  aluno?: Aluno;
  materia?: Materia;
  dataAula: string;
  horarioInicio: string;
  horarioFim: string;
  statusAula: string;
  linkReuniao?: string;
  valorAula: number;
  dataCriacao: string;
}
