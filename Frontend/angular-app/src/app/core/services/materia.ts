import { Injectable } from '@angular/core';
import { Materia } from '../../shared/models'; 
import { Observable, of } from 'rxjs'; 

@Injectable({
  providedIn: 'root'
})
export class MateriaService {

  private mockMaterias: Materia[] = [
    { id: 1, nome: 'Matemática', icone: '📐' }, 
    { id: 2, nome: 'Artes', icone: '🎨' },     
    { id: 3, nome: 'Vestibular', icone: '📚' }, 
    { id: 4, nome: 'Programação', icone: '💻' }, 
    { id: 5, nome: 'Inglês', icone: '🌎' }      
  ];

  constructor() { }

  getMaterias(): Observable<Materia[]> {
    return of(this.mockMaterias);
  }
}