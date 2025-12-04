import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { PasswordResetService } from '../../core/services/password-reset.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.scss']
})
export class ResetPasswordComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private srv: PasswordResetService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const tokenFromQuery = this.route.snapshot.queryParamMap.get('token') || '';
    this.form = this.fb.group({
      token: [tokenFromQuery, [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordsMatch });
  }

  private passwordsMatch(group: FormGroup) {
    const a = group.get('newPassword')?.value;
    const b = group.get('confirmPassword')?.value;
    return a === b ? null : { mismatch: true };
  }

  submit(): void {
    if (this.form.invalid) return;
    const token = this.form.get('token')!.value as string;
    const newPassword = this.form.get('newPassword')!.value as string;

    // debug antes do envio
    console.debug('[ResetPasswordComponent] token:', token);
    console.debug('[ResetPasswordComponent] newPassword (length):', newPassword?.length);

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.srv.resetPassword(token, newPassword).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Senha alterada com sucesso. Você já pode entrar com a nova senha.';
        setTimeout(() => this.router.navigate(['/auth/login']), 1800);
      },
      error: (err) => {
        console.error('resetPassword error', err);
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Erro ao alterar a senha. Verifique o token e tente novamente.';
      }
    });
  }
}