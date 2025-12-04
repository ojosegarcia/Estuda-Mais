import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Feedback } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = `${environment.apiUrl}/api/feedbacks`;

  constructor(private http: HttpClient) {}

  criarFeedback(feedback: Partial<Feedback>): Observable<Feedback> {
    return this.http. post<Feedback>(this.apiUrl, feedback);
  }

  buscarPorAula(aulaId: number): Observable<Feedback> {
    return this.http.get<Feedback>(`${this.apiUrl}/aula/${aulaId}`);
  }
}