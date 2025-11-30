import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Conquista } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class ConquistaService {
  private apiUrl = `${environment.apiUrl}/api/professores`;

  constructor(private http: HttpClient) {}

  listar(professorId: number): Observable<Conquista[]> {
    return this.http.get<Conquista[]>(`${this.apiUrl}/${professorId}/conquistas`);
  }

  criar(professorId: number, conquista: Partial<Conquista>): Observable<Conquista> {
    return this.http.post<Conquista>(`${this.apiUrl}/${professorId}/conquistas`, conquista);
  }

  atualizar(professorId: number, conquistaId: number, conquista: Partial<Conquista>): Observable<Conquista> {
    return this.http.put<Conquista>(`${this.apiUrl}/${professorId}/conquistas/${conquistaId}`, conquista);
  }

  deletar(professorId: number, conquistaId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${professorId}/conquistas/${conquistaId}`);
  }
}
