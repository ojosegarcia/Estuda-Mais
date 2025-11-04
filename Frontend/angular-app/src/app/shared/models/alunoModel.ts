import { Usuario } from './usuarioModel';

export interface Aluno extends Usuario {
  escolaridade?: string;
}
