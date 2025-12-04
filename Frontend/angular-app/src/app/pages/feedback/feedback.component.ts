
import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { FeedbackService } from '../../core/services/feedback.service';
import { AuthService } from '../../core/services/auth';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './feedback.html'
})
export class FeedbackComponent implements OnInit {
  @Input() aulaId!: number;
  @Input() aula?: any;

  feedbackForm!: FormGroup; // inicializamos no ngOnInit

  existingFeedback: any = null;
  currentUser: any = null;
  loading = false;
  error?: string;

  constructor(private fb: FormBuilder, private feedbackService: FeedbackService, private auth: AuthService) {
    this.currentUser = this.auth.getCurrentUser();
  }

  ngOnInit(): void {
    // cria o form aqui (fb já foi injetado)
    this.feedbackForm = this.fb.group({
      nota: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comentarioPublico: [''],
      comentarioPrivado: [''],
      recomenda: [true]
    });

    if (!this.aulaId && this.aula) this.aulaId = this.aula.id;
    if (this.aulaId) {
      this.feedbackService.getFeedbackByAula(this.aulaId).pipe(
        catchError(() => of(null))
      ).subscribe(res => {
        this.existingFeedback = res || null;
      });
    }
  }

  canSubmit(): boolean {
    const statusOk = !this.aula || this.aula.statusAula === 'REALIZADA';
    return !!this.currentUser && statusOk && !this.existingFeedback;
  }

  submit(): void {
    if (this.feedbackForm.invalid || !this.canSubmit()) return;
    this.loading = true;
    const v = this.feedbackForm.value;
    const payload: any = {
      idAula: this.aulaId,
      nota: v.nota,
      comentarioPublico: v.comentarioPublico,
      comentarioPrivado: v.comentarioPrivado,
      recomenda: v.recomenda
    };
    if (this.currentUser.tipoUsuario === 'ALUNO') payload.idAluno = this.currentUser.id;
    else payload.idProfessor = this.currentUser.id;

    this.feedbackService.postFeedback(payload).subscribe({
      next: () => {
        this.loading = false;
        alert('Feedback enviado');
        this.feedbackService.getFeedbackByAula(this.aulaId).subscribe(r => this.existingFeedback = r || null);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error || err?.message || 'Erro';
        alert('Erro ao enviar feedback: ' + this.error);
      }
    });
  }
}