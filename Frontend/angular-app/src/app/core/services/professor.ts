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

  // Busca professores que ensinam uma matéria específica
  getProfessoresPorMateria(materiaId: number): Observable<Professor[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}?tipoUsuario=PROFESSOR`).pipe(
      map(usuarios => {
        console.log('Todos professores:', usuarios);
        console.log('Buscando matéria ID:', materiaId);
        
        // Filtra apenas professores aprovados que têm a matéria desejada
        const professoresFiltrados = usuarios.filter(user => {
          const prof = user as Professor;
          const temMateria = prof.materias?.some(m => m.id === materiaId);
          console.log(`Professor ${prof.nomeCompleto}: aprovado=${prof.aprovado}, temMateria=${temMateria}`);
          return prof.aprovado && temMateria;
        }) as Professor[];
        
        console.log('Professores filtrados:', professoresFiltrados);
        return professoresFiltrados;
      })
    );
  }

  // Busca um professor específico por ID
  getProfessorById(id: number): Observable<Professor | undefined> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`).pipe(
      map(usuario => {
        if (usuario.tipoUsuario === 'PROFESSOR') {
          return usuario as Professor;
        }
        return undefined;
      })
    );
  }
}