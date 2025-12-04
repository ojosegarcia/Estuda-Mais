
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { AulaService } from '../../core/services/aula';
import { AuthService } from '../../core/services/auth';
import { FeedbackComponent } from '../feedback/feedback.component';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-aula-detail',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FeedbackComponent],
  templateUrl: './aula-detail.component.html'
  // removido styleUrls para evitar erro se o arquivo CSS não existir
})
export class AulaDetailComponent implements OnInit {
  aula: any = null;
  usuario: any = null;
  isConcluding = false;
  loading = true;
  error?: string;

  constructor(
    private route: ActivatedRoute,
    private aulaService: AulaService,
    private auth: AuthService
  ) {
    this.usuario = this.auth.getCurrentUser();
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : null;
    if (!id) {
      this.error = 'ID da aula inválido';
      this.loading = false;
      return;
    }

    this.aulaService.getAulasPorUsuarioLogado().pipe(
      catchError(() => of([]))
    ).subscribe((aulas: any[]) => {
      const found = aulas.find(a => a.id === id);
      if (found) {
        this.aula = found;
        this.loading = false;
      } else {
        this.aulaService['http'].get<any>(`${this.aulaService['apiUrl']}/${id}`).pipe(
          catchError(() => of(null))
        ).subscribe(a => {
          this.aula = a;
          this.loading = false;
        });
      }
    });
  }

  podeConcluir(): boolean {
    if (!this.usuario || !this.aula) return false;
    const ehAluno = this.usuario.tipoUsuario === 'ALUNO' && this.aula.idAluno === this.usuario.id;
    const ehProfessor = this.usuario.tipoUsuario === 'PROFESSOR' && this.aula.idProfessor === this.usuario.id;
    return (ehAluno || ehProfessor) && this.aula.statusAula !== 'REALIZADA';
  }

  concluirAula(): void {
    if (!this.aula) return;
    this.isConcluding = true;
    this.aulaService.concluirAula(this.aula.id).subscribe({
      next: (a) => {
        this.aula = a;
        this.isConcluding = false;
        alert('Aula marcada como realizada');
      },
      error: (err) => {
        this.isConcluding = false;
        console.error(err);
        alert('Erro ao marcar aula como realizada: ' + (err?.error || err?.message || err));
      }
    });
  }
}