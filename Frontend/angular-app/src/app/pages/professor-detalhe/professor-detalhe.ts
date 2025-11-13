import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Professor } from '../../shared/models';
import { ProfessorService } from '../../core/services/professor';
import { AulaService } from '../../core/services/aula';
import { AuthService } from '../../core/services/auth';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-professor-detalhe',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './professor-detalhe.html',
  styleUrls: ['./professor-detalhe.css']
})
export class ProfessorDetalheComponent implements OnInit {

  professor$: Observable<Professor | undefined> | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private professorService: ProfessorService,
    private aulaService: AulaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const professorId = Number(this.route.snapshot.params['id']);
    console.log('Professor ID:', professorId);
    if (professorId) {
      this.professor$ = this.professorService.getProfessorById(professorId);
      this.professor$.subscribe(prof => {
        console.log('Professor carregado:', prof);
      });
    }
  }

  agendarAula(professor: Professor): void {
    const usuario = this.authService.getCurrentUser();
    
    if (!usuario) {
      alert('Você precisa estar logado para agendar uma aula!');
      this.router.navigate(['/auth/login']);
      return;
    }

    if (usuario.tipoUsuario !== 'ALUNO') {
      alert('Apenas alunos podem agendar aulas!');
      return;
    }

    // Pega a primeira matéria do professor para simplificar
    const materiaId = professor.materias && professor.materias.length > 0 
      ? Number(professor.materias[0].id)
      : 1;

    const materia = professor.materias && professor.materias.length > 0
      ? professor.materias[0]
      : { id: 1, nome: 'Matéria não especificada' };

    // Cria uma aula para amanhã às 14h (pode ser melhorado com um modal)
    const amanha = new Date();
    amanha.setDate(amanha.getDate() + 1);
    const dataAula = amanha.toISOString().split('T')[0];

    const novaAula = {
      idProfessor: Number(professor.id),
      idAluno: Number(usuario.id),
      idMateria: materiaId,
      dataAula: dataAula,
      horarioInicio: '14:00',
      horarioFim: '15:00',
      valorAula: professor.valorHora || 0,
      aluno: usuario, // Inclui objeto completo do aluno
      professor: professor, // Inclui objeto completo do professor
      materia: materia // Inclui objeto completo da matéria
    };

    this.aulaService.solicitarAula(novaAula).subscribe({
      next: () => {
        alert('Aula solicitada com sucesso! Aguarde a confirmação do professor.');
        this.router.navigate(['/minhas-aulas']);
      },
      error: (err) => {
        console.error('Erro ao solicitar aula:', err);
        alert('Erro ao solicitar aula. Tente novamente.');
      }
    });
  }

  getInitials(nomeCompleto: string | undefined): string {
    if (!nomeCompleto) return '??';
    const names = nomeCompleto.trim().split(' ').filter(n => n.length > 0);
    if (names.length === 0) return '??';
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }
}