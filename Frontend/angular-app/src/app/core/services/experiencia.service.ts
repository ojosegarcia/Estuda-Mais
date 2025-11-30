import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ExperienciaProfissional } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class ExperienciaService {
  private apiUrl = `${environment.apiUrl}/api/professores`;

  constructor(private http: HttpClient) {}

  listar(professorId: number): Observable<ExperienciaProfissional[]> {
    return this.http.get<ExperienciaProfissional[]>(`${this.apiUrl}/${professorId}/experiencias`);
  }

  criar(professorId: number, experiencia: Partial<ExperienciaProfissional>): Observable<ExperienciaProfissional> {
    return this.http.post<ExperienciaProfissional>(`${this.apiUrl}/${professorId}/experiencias`, experiencia);
  }

  atualizar(professorId: number, expId: number, experiencia: Partial<ExperienciaProfissional>): Observable<ExperienciaProfissional> {
    return this.http.put<ExperienciaProfissional>(`${this.apiUrl}/${professorId}/experiencias/${expId}`, experiencia);
  }

  deletar(professorId: number, expId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${professorId}/experiencias/${expId}`);
  }
}
