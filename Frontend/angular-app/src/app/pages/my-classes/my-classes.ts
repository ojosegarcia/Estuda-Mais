import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Usuario } from '../../shared/models/usuarioModel';

@Component({
  selector: 'app-my-classes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-classes.html',
  styleUrls: ['./my-classes.css']
})
export class MyClassesComponent implements OnInit {
  currentUser: Usuario | null = null;

  // Dados mockados para demonstração
  aulasAluno = [
    {
      id: 1,
      materia: 'Matemática',
      professor: 'Prof. João Silva',
      data: '2025-11-05',
      horario: '14:00 - 15:00',
      status: 'Confirmada'
    },
    {
      id: 2,
      materia: 'Física',
      professor: 'Prof. Maria Santos',
      data: '2025-11-08',
      horario: '16:00 - 17:00',
      status: 'Pendente'
    }
  ];

  aulasProfessor = [
    {
      id: 1,
      materia: 'Matemática',
      aluno: 'Carlos Oliveira',
      data: '2025-11-05',
      horario: '14:00 - 15:00',
      status: 'Confirmada'
    },
    {
      id: 2,
      materia: 'Álgebra',
      aluno: 'Ana Paula',
      data: '2025-11-06',
      horario: '10:00 - 11:00',
      status: 'Pendente'
    },
    {
      id: 3,
      materia: 'Cálculo',
      aluno: 'Pedro Costa',
      data: '2025-11-07',
      horario: '15:00 - 16:00',
      status: 'Pendente'
    }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    const userData = localStorage.getItem('usuarioLogado');
    if (userData) {
      this.currentUser = JSON.parse(userData);
      console.log('MyClasses - Current User:', this.currentUser);
      console.log('MyClasses - Tipo de Usuário:', this.currentUser?.tipoUsuario);
      console.log('MyClasses - isProfessor:', this.isProfessor());
      console.log('MyClasses - isAluno:', this.isAluno());
    }
  }

  isProfessor(): boolean {
    return this.currentUser?.tipoUsuario === 'PROFESSOR';
  }

  isAluno(): boolean {
    return this.currentUser?.tipoUsuario === 'ALUNO';
  }

  // Getters para contar aulas por status
  get aulasConfirmadas(): number {
    return this.aulasProfessor.filter(aula => aula.status === 'Confirmada').length;
  }

  get aulasPendentes(): number {
    return this.aulasProfessor.filter(aula => aula.status === 'Pendente').length;
  }

  // Métodos placeholder para ações futuras
  confirmarAula(aulaId: number): void {
    alert(`Confirmar aula #${aulaId} - Funcionalidade em desenvolvimento`);
  }

  cancelarAula(aulaId: number): void {
    alert(`Cancelar aula #${aulaId} - Funcionalidade em desenvolvimento`);
  }

  reagendarAula(aulaId: number): void {
    alert(`Reagendar aula #${aulaId} - Funcionalidade em desenvolvimento`);
  }
}
