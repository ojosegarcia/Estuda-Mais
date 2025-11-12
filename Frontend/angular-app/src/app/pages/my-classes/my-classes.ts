import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; // Importe RouterLink
import { Usuario } from '../../shared/models/usuarioModel';
import { Aula } from '../../shared/models'; // Importe o model Aula
import { AulaService } from '../../core/services/aula'; // Importe o AulaService
import { AuthService } from '../../core/services/auth'; // Importe o AuthService
import { Observable } from 'rxjs'; // Importe Observable

@Component({
  selector: 'app-my-classes',
  standalone: true,
  imports: [CommonModule, RouterLink], 
  templateUrl: './my-classes.html',
  styleUrls: ['./my-classes.css']
})
export class MyClassesComponent implements OnInit {
  currentUser: Usuario | null = null;
  isLoading = true;


  aulas$!: Observable<Aula[]>;
  
  totalAulas = 0;
  aulasConfirmadas = 0;
  aulasPendentes = 0;

  constructor(
    private router: Router,
    private authService: AuthService,
    private aulaService: AulaService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.carregarAulas();
  }

  carregarAulas(): void {
    if (!this.currentUser) {
      this.isLoading = false;
      return;
    }
    
    this.isLoading = true;
    this.aulas$ = this.aulaService.getAulasPorUsuarioLogado();

    this.aulas$.subscribe(aulas => {
      if (this.isProfessor()) {
        this.totalAulas = aulas.length;
        this.aulasConfirmadas = aulas.filter(a => a.statusAula === 'CONFIRMADA').length;
        this.aulasPendentes = aulas.filter(a => a.statusAula === 'SOLICITADA').length;
      }
      this.isLoading = false;
    });
  }

  isProfessor(): boolean {
    return this.currentUser?.tipoUsuario === 'PROFESSOR';
  }

  isAluno(): boolean {
    return this.currentUser?.tipoUsuario === 'ALUNO';
  }

  // --- Métodos de Ação ---
  // Agora eles chamam o serviço. Não precisamos recarregar a lista,
  // pois o BehaviorSubject no serviço fará isso automaticamente.

  confirmarAula(aulaId: number): void {
    this.aulaService.aceitarAula(aulaId).subscribe(() => {
      console.log('Aula confirmada!');
    });
  }

  cancelarAula(aulaId: number): void {
    if (confirm('Tem certeza que deseja cancelar esta aula?')) {
      this.aulaService.cancelarAula(aulaId).subscribe(() => {
        console.log('Aula cancelada!');
      });
    }
  }

  recusarAula(aulaId: number): void {
    this.aulaService.recusarAula(aulaId).subscribe(() => {
      console.log('Aula recusada!');
    });
  }

  reagendarAula(aulaId: number): void {
    alert(`Reagendar aula #${aulaId} - Funcionalidade em desenvolvimento`);
  }

  // Adicione esta função para o avatar do professor
  getIniciais(nome: string): string {
    if (!nome) return '??';
    const names = nome.split(' ');
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }

  // Função para formatar data (ex: 2025-11-15 -> 15/11/2025)
  formatarData(data: string): string {
    if (!data) return 'Data não informada';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  // Função para obter o label do status em português
  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'SOLICITADA': 'Aguardando',
      'CONFIRMADA': 'Confirmada',
      'RECUSADA': 'Recusada',
      'CANCELADA': 'Cancelada',
      'REALIZADA': 'Realizada'
    };
    return labels[status] || status;
  }

  // Função para obter a cor do status
  getStatusClass(status: string): string {
    return `status-${status.toLowerCase()}`;
  }
}