export interface Disponibilidade {
  id: number;
  idProfessor: number;
  diaSemana: 'SEGUNDA' | 'TERCA' | 'QUARTA' | 'QUINTA' | 'SEXTA' | 'SABADO' | 'DOMINGO';
  horarioInicio: string; // Formato: "HH:mm"
  horarioFim: string;    // Formato: "HH:mm"
  ativo: boolean;
}
