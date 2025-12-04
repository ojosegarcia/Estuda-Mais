import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { PasswordResetService } from '../../core/services/password-reset.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.scss']
})
export class ForgotPasswordComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  infoMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private srv: PasswordResetService, private router: Router) {}

  ngOnInit(): void {
    this.form = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  }

  send(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.infoMessage = '';
    this.errorMessage = '';
    const email = this.form.get('email')!.value as string;
    this.srv.forgotPassword(email).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res?.token) {
          this.router.navigate(['/auth/reset-password'], { queryParams: { token: res.token } });
        } else {
          this.infoMessage = 'Se existe uma conta com esse e‑mail, você receberá instruções por e‑mail.';
        }
      },
      error: (err) => {
        this.loading = false;
        console.error('forgotPassword error', err);
        this.errorMessage = 'Erro ao solicitar recuperação. Tente novamente mais tarde.';
      }
    });
  }
}