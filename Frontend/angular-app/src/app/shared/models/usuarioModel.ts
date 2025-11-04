export interface Usuario {
  id: number; 
  nomeCompleto?: string;
  email: string;
  password?: string;
  telefone?: string;
  dataNascimento?: Date;
  sexo?: string;
  fotoPerfil?: string;
  dataCadastro?: string;
  ativo?: boolean;
  tipoUsuario: 'ALUNO' | 'PROFESSOR';
}
