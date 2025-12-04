import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FeedbackRequest {
  idAula: number;
  idAluno?: number;
  idProfessor?: number;
  nota: number;
  comentarioPrivado?: string;
  comentarioPublico?: string;
  recomenda?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private api = `${environment.apiUrl}/api/feedbacks`;

  constructor(private http: HttpClient) {}

  postFeedback(payload: FeedbackRequest): Observable<any> {
    return this.http.post<any>(this.api, payload);
  }

  getFeedbackByAula(aulaId: number): Observable<any> {
    return this.http.get<any>(`${this.api}/aula/${aulaId}`);
  }
}