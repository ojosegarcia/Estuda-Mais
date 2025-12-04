import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {
  private base = `${environment.apiUrl}/api/auth`;
  private mockMode = !!(environment as any).mockPasswordReset;

  constructor(private http: HttpClient) {}

  forgotPassword(email: string): Observable<any> {
    if (this.mockMode) {
      const testEmail = 'test@example.com';
      if (email.toLowerCase() === testEmail) {
        return of({ token: 'dev-token-123456' }).pipe(delay(600));
      } else {
        return of({ message: 'If the email exists, an email was sent.' }).pipe(delay(600));
      }
    }
    return this.http.post(`${this.base}/forgot-password`, { email });
  }

  // envia vários nomes de campo (fallback) para aumentar compatibilidade com diferentes backends
  resetPassword(token: string, newPassword: string): Observable<any> {
    if (this.mockMode) {
      if (token === 'dev-token-123456') {
        return of({ ok: true }).pipe(delay(600));
      } else {
        return throwError(() => ({ error: { message: 'Token inválido ou expirado (mock).' } })).pipe(delay(600));
      }
    }

    const payload: any = {
      token,
      rawPassword: newPassword,
      password: newPassword,
      newPassword
    };

    console.debug('[PasswordResetService] POST', `${this.base}/reset-password`, payload);
    return this.http.post(`${this.base}/reset-password`, payload);
  }
}