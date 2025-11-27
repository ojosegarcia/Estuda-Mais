import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; 
import { AuthService} from '../../core/services/auth'; 
import { Aluno } from '../../shared/models/alunoModel';
import { Professor } from '../../shared/models/professorModel';
import { Materia, Usuario } from '../../shared/models';
import { Observable } from 'rxjs'; 

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule], 
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class PerfilComponent implements OnInit {


  currentUser$: Observable<Usuario | null>;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }
  ngOnInit(): void {
  }
  isAluno(user: Usuario): boolean {
    return user?.tipoUsuario === 'ALUNO';
  }

  isProfessor(user: Usuario): boolean {
    return user?.tipoUsuario === 'PROFESSOR';
  }
    asAluno(user: Usuario): Aluno {
    return user as Aluno;
  }
  asProfessor(user: Usuario): Professor {
    return user as Professor;
  }
  getInteresseLabel(interesse: string | undefined): string {
    if (!interesse) return '';
    const labels: { [key: string]: string } = {
      'APRENDER_NOVO': '🌟 Aprender algo novo',
      'REFORCAR_CONHECIMENTO': '💪 Reforçar o que já sei',
      'PREPARAR_CONQUISTA': '🎯 Me preparar para uma conquista'
    };
    return labels[interesse] || interesse;
  }
  getUserInitials(nome: string | undefined): string {
    if (!nome) return '??';
    const names = nome.trim().split(' ').filter(n => n.length > 0);
    if (names.length === 0) return '??';
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }
  editarPerfil(): void {
    this.router.navigate(['/perfil/editar']);
  }
}