import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Usuario } from '../../shared/models/usuarioModel';
import { Aluno } from '../../shared/models/alunoModel';
import { Professor } from '../../shared/models/professorModel';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class PerfilComponent implements OnInit {
  currentUser: Usuario | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const userData = localStorage.getItem('usuarioLogado');
    if (userData) {
      this.currentUser = JSON.parse(userData);
    }
  }

  isAluno(): boolean {
    return this.currentUser?.tipoUsuario === 'ALUNO';
  }

  isProfessor(): boolean {
    return this.currentUser?.tipoUsuario === 'PROFESSOR';
  }

  getEscolaridade(): string | undefined {
    return this.isAluno() ? (this.currentUser as Aluno).escolaridade : undefined;
  }

  getSobre(): string | undefined {
    return this.isProfessor() ? (this.currentUser as Professor).sobre : undefined;
  }

  getUserInitials(): string {
    if (!this.currentUser?.nomeCompleto) return '??';
    const names = this.currentUser.nomeCompleto.split(' ');
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  editarPerfil(): void {
    alert('Função de editar perfil em desenvolvimento');
  }

  editarDisponibilidade(): void {
    alert('Função de editar disponibilidade em desenvolvimento');
  }

  editarMaterias(): void {
    alert('Função de editar matérias em desenvolvimento');
  }
}
