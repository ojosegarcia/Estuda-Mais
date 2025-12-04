
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { PasswordResetService } from '../../core/services/password-reset.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.html',
})
export class ForgotPasswordComponent implements OnInit {
  form!: FormGroup;
  token?: string;
  loading = false;

  constructor(private fb: FormBuilder, private srv: PasswordResetService) {}

  ngOnInit(): void {
    this.form = this.fb.group({ email: ['', [Validators.required, Validators.email]] });
  }

  send(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const email = this.form.get('email')!.value as string;
    this.srv.forgotPassword(email).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.token = res?.token; // dev
        alert('Se o e-mail existir, um token foi gerado (dev).');
      },
      error: () => {
        this.loading = false;
        alert('Erro ao solicitar token');
      }
    });
  }
}