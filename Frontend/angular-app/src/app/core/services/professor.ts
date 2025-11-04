import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators'; 
import { Professor } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {

  private mockProfessores: Professor[] = [
    {
      id: 101, nomeCompleto: 'Dr. Ana Silva', email: 'ana@email.com', tipoUsuario: 'PROFESSOR', aprovado: true,
      sobre: 'Doutora em Matemática pela USP, 10 anos de experiência.', valorHora: 80,
      materias: [{ id: 1, nome: 'Matemática' }, { id: 3, nome: 'Vestibular' }],
      experiencias: [{ id: 1, cargo: 'Professora', instituicao: 'USP', periodo: '2010-2020', descricao: 'Ensino de Cálculo I' }],
      conquistas: [{ id: 1, tituloConquista: 'Prêmio X', ano: 2019, descricao: 'Melhor tese' }],
      disponibilidades: [{ id: 1, diaSemana: 'SEGUNDA', horarioInicio: '14:00', horarioFim: '15:00', ativo: true }]
    },
    {
      id: 102, nomeCompleto: 'Bruno Gomes', email: 'bruno@email.com', tipoUsuario: 'PROFESSOR', aprovado: true,
      sobre: 'Desenvolvedor Sênior e entusiasta de Angular e React.', valorHora: 100,
      materias: [{ id: 4, nome: 'Programação' }],
      experiencias: [{ id: 1, cargo: 'Sênior Developer', instituicao: 'Google', periodo: '2015-2023', descricao: 'Dev de frontend' }]
    },
    {
      id: 103, nomeCompleto: 'Carla Dias', email: 'carla@email.com', tipoUsuario: 'PROFESSOR', aprovado: true,
      sobre: 'Professora de inglês com intercâmbio na Inglaterra.', valorHora: 70,
      materias: [{ id: 5, nome: 'Inglês' }]
    },
  ];

  constructor() { }

  getProfessoresPorMateria(materiaId: number): Observable<Professor[]> {
    const professoresFiltrados = this.mockProfessores.filter(prof => 
      prof.materias?.some(materia => materia.id === materiaId)
    );
    return of(professoresFiltrados).pipe(delay(500));
  }

  // NOVO MÉTODO PARA A PÁGINA DE DETALHES
  getProfessorById(id: number): Observable<Professor | undefined> {
    const professor = this.mockProfessores.find(prof => prof.id === id);
    return of(professor).pipe(delay(300));
  }
}