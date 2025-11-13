import { Component, OnInit } from '@angular/core'; 
import { CommonModule } from '@angular/common'; 
import { RouterModule, Router } from '@angular/router'; 

import { MateriaService } from '../../core/services/materia'; 
import { Materia } from '../../shared/models';
import { AuthService } from '../../core/services/auth';
import { ProfileCompletionModalComponent } from '../../shared/components/profile-completion-modal/profile-completion-modal';

@Component({
  selector: 'app-home',
  standalone: true, 
  imports: [
    CommonModule,
    RouterModule,
    ProfileCompletionModalComponent
  ],
  templateUrl: './home.component.html', 
  styleUrls: ['./home.component.css']  
})
export class HomeComponent implements OnInit { 
  
  materias: Materia[] = [];
  showProfileModal = false;

  constructor(
    private materiaService: MateriaService,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.materiaService.getMaterias().subscribe(dados => {
      this.materias = dados; 
    });

    // Verifica se o perfil está completo
    if (!this.authService.isProfileComplete()) {
      this.showProfileModal = true;
    }
  }

  closeModal(): void {
    this.showProfileModal = false;
  }

  buscarPorMateria(materiaId: number): void {
    this.router.navigate(['/busca'], { queryParams: { materiaId: materiaId } });
  }
}