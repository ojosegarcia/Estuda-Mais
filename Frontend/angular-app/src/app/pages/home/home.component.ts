import { Component, OnInit } from '@angular/core'; 
import { CommonModule } from '@angular/common'; 
import { RouterModule, Router } from '@angular/router'; 

import { MateriaService } from '../../core/services/materia'; 
import { Materia } from '../../shared/models'; 

@Component({
  selector: 'app-home',
  standalone: true, 
  imports: [
    CommonModule,
    RouterModule 
  ],
  templateUrl: './home.component.html', 
  styleUrls: ['./home.component.css']  
})
export class HomeComponent implements OnInit { 
  

  materias: Materia[] = [];

  constructor(
    private materiaService: MateriaService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.materiaService.getMaterias().subscribe(dados => {
      this.materias = dados; 
    });
  }

  buscarPorMateria(materiaId: number): void {
    console.log('Buscando por matéria com ID:', materiaId);
    
    this.router.navigate(['/busca'], { queryParams: { materiaId: materiaId } });
  }
}