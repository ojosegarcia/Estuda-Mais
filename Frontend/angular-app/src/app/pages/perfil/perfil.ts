import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { Aluno } from '../../shared/models/alunoModel';
import { Professor } from '../../shared/models/professorModel';
import { Usuario, Materia } from '../../shared/models';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class PerfilComponent implements OnInit {
  currentUser: Usuario | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
  }


  isAluno(): boolean {
    return this.currentUser?.tipoUsuario === 'ALUNO';
  }

  isProfessor(): boolean {
    return this.currentUser?.tipoUsuario === 'PROFESSOR';
  }

  getAluno(): Aluno {
    return this.currentUser as Aluno;
  }

  getProfessor(): Professor {
    return this.currentUser as Professor;
  }

  getUserInitials(): string {
    if (!this.currentUser?.nomeCompleto) return '??';
    const names = this.currentUser.nomeCompleto.split(' ');
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }

  // Métodos auxiliares para acessar propriedades específicas de tipo
  getEscolaridade(): string | undefined {
    if (this.isAluno()) {
      return (this.currentUser as Aluno).escolaridade;
    }
    return undefined;
  }

  getInteresse(): string | undefined {
    if (this.isAluno()) {
      return (this.currentUser as Aluno).interesse;
    }
    return undefined;
  }

  getInteresseLabel(): string {
    const interesse = this.getInteresse();
    if (!interesse) return '';
    
    const labels: { [key: string]: string } = {
      'APRENDER_NOVO': '🌟 Aprender algo novo',
      'REFORCAR_CONHECIMENTO': '💪 Reforçar o que já sei',
      'PREPARAR_CONQUISTA': '🎯 Me preparar para uma conquista'
    };
    
    return labels[interesse] || interesse;
  }

  getSobre(): string | undefined {
    if (this.isProfessor()) {
      return (this.currentUser as Professor).sobre;
    }
    return undefined;
  }

  getMaterias(): Materia[] {
    if (this.isProfessor()) {
      const professor = this.currentUser as Professor;
      return professor.materias || [];
    }
    return [];
  }

  editarPerfil(): void {
    this.router.navigate(['/perfil/editar']);
  }

  editarDisponibilidade(): void {
    alert('Função de editar disponibilidade em desenvolvimento');
  }

  editarMaterias(): void {
    alert('Função de editar matérias em desenvolvimento');
  }
}