import { Usuario } from './usuarioModel';
import { Materia } from './materiaModel';
import { Disponibilidade } from './disponibilidadeModel';
import { ExperienciaProfissional } from './experienciaModel';
import { Conquista } from './conquistaModel';
import { Feedback } from './feedbackModel';

export interface Professor extends Usuario {
  sobre?: string;
  metodologia?: string;
  valorHora?: number;
  fotoCertificado?: string;
  aprovado: boolean;
  materias?: Materia[];
  disponibilidades?: Disponibilidade[];
  experiencias?: ExperienciaProfissional[];
  conquistas?: Conquista[];
  feedbacks?: Feedback[];
}
