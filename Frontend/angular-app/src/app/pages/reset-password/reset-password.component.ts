
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { PasswordResetService } from '../../core/services/password-reset.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reset-password.html'
})
export class ResetPasswordComponent implements OnInit {
  form!: FormGroup;
  loading = false;

  constructor(private fb: FormBuilder, private srv: PasswordResetService) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      token: ['', Validators.required],
      novaSenha: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  reset(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const token = this.form.get('token')!.value as string;
    const novaSenha = this.form.get('novaSenha')!.value as string;
    this.srv.resetPassword(token, novaSenha).subscribe({
      next: () => {
        this.loading = false;
        alert('Senha alterada com sucesso. Faça login com a nova senha.');
      },
      error: (err) => {
        this.loading = false;
        alert('Erro ao resetar senha: ' + (err?.error?.error || err?.message || ''));
      }
    });
  }
}