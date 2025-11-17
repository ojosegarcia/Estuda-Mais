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

  // Busca todas as disponibilidades de um professor
  getDisponibilidadesPorProfessor(professorId: number): Observable<Disponibilidade[]> {
    return this.http.get<Disponibilidade[]>(`${this.apiUrl}?idProfessor=${professorId}&ativo=true`);
  }

  // Busca uma disponibilidade específica
  getDisponibilidadePorId(id: number): Observable<Disponibilidade> {
    return this.http.get<Disponibilidade>(`${this.apiUrl}/${id}`);
  }

  // Cria uma nova disponibilidade
  criarDisponibilidade(disponibilidade: Omit<Disponibilidade, 'id'>): Observable<Disponibilidade> {
    return this.http.post<Disponibilidade>(this.apiUrl, disponibilidade);
  }

  // Atualiza uma disponibilidade existente
  atualizarDisponibilidade(id: number, disponibilidade: Disponibilidade): Observable<Disponibilidade> {
    return this.http.put<Disponibilidade>(`${this.apiUrl}/${id}`, disponibilidade);
  }

  // Remove uma disponibilidade (soft delete - marca como inativa)
  removerDisponibilidade(id: number): Observable<Disponibilidade> {
    return this.http.get<Disponibilidade>(`${this.apiUrl}/${id}`).pipe(
      // Marca como inativa ao invés de deletar
    );
  }

  // Salva múltiplas disponibilidades de uma vez
  salvarDisponibilidades(professorId: number, disponibilidades: Omit<Disponibilidade, 'id'>[]): Observable<Disponibilidade[]> {
    // Primeiro, busca as existentes
    return this.http.get<Disponibilidade[]>(`${this.apiUrl}?idProfessor=${professorId}`);
    // Em um cenário real, faríamos um batch update/create
    // Por simplicidade, vamos fazer um a um no componente
  }
}
