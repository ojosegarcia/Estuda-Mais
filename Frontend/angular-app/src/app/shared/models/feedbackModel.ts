export interface Feedback {
  id: number;
  idAula: number;
  idAluno: number;
  idProfessor: number;
  nota: number;
  comentarioPrivado?: string;
  comentarioPublico?: string;
  dataFeedback: string;
  recomenda: boolean;
}
