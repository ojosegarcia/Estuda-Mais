import { Injectable } from '@angular/core';
import { Materia } from '../../shared/models'; 
import { Observable, of } from 'rxjs'; 
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MateriaService {

  private apiUrl = 'http://localhost:3000/materias';

  constructor(private http: HttpClient) { }

  getMaterias(): Observable<Materia[]> {
    return this.http.get<Materia[]>(this.apiUrl);
  }

  getMateriaPorId(id: number): Observable<Materia> {
    return this.http.get<Materia>(`${this.apiUrl}/${id}`);
  }
  
  findOrCreateMateria(nomeMateria: string): Observable<Materia> {
    return this.http.get<Materia[]>(`${this.apiUrl}?nome_like=${nomeMateria}`).pipe(
      switchMap(materias => {
        if (materias.length > 0) {
          return of(materias[0]);
        } else {
          const novaMateria: Omit<Materia, 'id'> = {
            nome: nomeMateria,
            icone: "🆕", 
            descricao: "Matéria adicionada pelo professor"
          };
          return this.http.post<Materia>(this.apiUrl, novaMateria);
        }
      })
    );
  }
}


