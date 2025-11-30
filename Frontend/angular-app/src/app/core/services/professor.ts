import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Professor, Usuario } from '../../shared/models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {

  private apiUrl = `${environment.apiUrl}/api/professores`;

  constructor(private http: HttpClient) { }
  
  getProfessoresPorMateria(materiaId: number): Observable<Professor[]> {
    // Backend retorna todos os professores, filtramos no cliente
    return this.http.get<Professor[]>(`${this.apiUrl}`).pipe(
      map(professores => {
        return professores.filter(prof => {
          const temMateria = prof.materias?.some(m => Number(m.id) === Number(materiaId));
          return prof.aprovado && temMateria;
        });
      })
    );
  }
  
  getProfessorById(id: number): Observable<Professor | undefined> {
    return this.http.get<Professor>(`${this.apiUrl}/${Number(id)}`).pipe(
      map(professor => professor)
    );
  }
}