import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {
  private api = `${environment.apiUrl}/api/auth`;

  constructor(private http: HttpClient) {}

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.api}/forgot-password`, { email });
  }

  resetPassword(token: string, novaSenha: string): Observable<any> {
    return this.http.post(`${this.api}/reset-password`, { token, novaSenha });
  }
}