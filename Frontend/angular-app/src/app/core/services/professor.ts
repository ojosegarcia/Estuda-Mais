import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Professor, Usuario } from '../../shared/models';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProfessorService {

  private apiUrl = 'http://localhost:3000/usuarios';

  constructor(private http: HttpClient) { }
  getProfessoresPorMateria(materiaId: number): Observable<Professor[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}?tipoUsuario=PROFESSOR`).pipe(
      map(usuarios => {
        const professoresFiltrados = usuarios.filter(user => {
          const prof = user as Professor;
          const temMateria = prof.materias?.some(m => Number(m.id) === Number(materiaId));
          return prof.aprovado && temMateria;
        }) as Professor[];

        return professoresFiltrados;
      })
    );
  }
  getProfessorById(id: number): Observable<Professor | undefined> {
    return this.http.get<Usuario>(`${this.apiUrl}/${Number(id)}`).pipe(
      map(usuario => {
        if (usuario && usuario.tipoUsuario === 'PROFESSOR') {
          return usuario as Professor;
        }
        return undefined;
      })
    );
  }
}