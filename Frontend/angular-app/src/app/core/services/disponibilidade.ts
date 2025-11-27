import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Disponibilidade } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class DisponibilidadeService {

  private apiUrl = 'http://localhost:3000/disponibilidades';

  constructor(private http: HttpClient) { }

  getDisponibilidadesPorProfessor(professorId: number): Observable<Disponibilidade[]> {
    return this.http.get<Disponibilidade[]>(`${this.apiUrl}?idProfessor=${professorId}&ativo=true`);
  }

  getDisponibilidadePorId(id: number): Observable<Disponibilidade> {
    return this.http.get<Disponibilidade>(`${this.apiUrl}/${id}`);
  }

  criarDisponibilidade(disponibilidade: Omit<Disponibilidade, 'id'>): Observable<Disponibilidade> {
    return this.http.post<Disponibilidade>(this.apiUrl, disponibilidade);
  }

  atualizarDisponibilidade(id: number, disponibilidade: Disponibilidade): Observable<Disponibilidade> {
    return this.http.put<Disponibilidade>(`${this.apiUrl}/${id}`, disponibilidade);
  }

  removerDisponibilidade(id: number): Observable<Disponibilidade> {
    return this.http.get<Disponibilidade>(`${this.apiUrl}/${id}`).pipe(
    );
  }

  salvarDisponibilidades(professorId: number, disponibilidades: Omit<Disponibilidade, 'id'>[]): Observable<Disponibilidade[]> {
    return this.http.get<Disponibilidade[]>(`${this.apiUrl}?idProfessor=${professorId}`);
  }
}
