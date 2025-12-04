import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router'; 
import { Usuario } from '../../shared/models/usuarioModel';
import { Professor } from '../../shared/models/professorModel';
import { Aula, Feedback } from '../../shared/models'; 
import { AulaService } from '../../core/services/aula';
import { AuthService } from '../../core/services/auth';
import { FeedbackService } from '../../core/services/feedback';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-my-classes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], 
  templateUrl: './my-classes.html',
  styleUrls: ['./my-classes.css']
})
export class MyClassesComponent implements OnInit {
  currentUser: Usuario | null = null;
  isLoading = true;
  aulas$!: Observable<Aula[]>;
  aulas: Aula[] = [];
  
  totalAulas = 0;
  aulasConfirmadas = 0;
  aulasPendentes = 0;

  // Modal de Link
  showLinkModal = false;
  aulaParaAceitar: Aula | null = null;
  linkReuniao = '';
  usarLinkPadrao = false;
  linkInvalido = false;

  // NOVO: Modal de Feedback
  showFeedbackModal = false;
  aulaParaFeedback: Aula | null = null;
  feedbackComentario = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private aulaService: AulaService,
    private feedbackService: FeedbackService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.carregarAulas();
    this.carregarLinkPadrao();
  }

  carregarLinkPadrao(): void {
    if (this.isProfessor() && this.currentUser) {
      const professor = this.currentUser as Professor;
      if (professor.linkPadraoAula && professor.usarLinkPadrao) {
        this.linkReuniao = professor.linkPadraoAula;
        this.usarLinkPadrao = true;
      }
    }
  }

  carregarAulas(): void {
    if (! this.currentUser) {
      this.isLoading = false;
      return;
    }
    
    this.isLoading = true;
    this.aulas$ = this.aulaService.getAulasPorUsuarioLogado(). pipe(
      map(aulas => this.ordenarAulasPorStatus(aulas))
    );

    this.aulas$.subscribe(aulas => {
      this.aulas = aulas;
      
      if (this.isProfessor()) {
        this.totalAulas = aulas.length;
        this.aulasConfirmadas = aulas.filter(a => a.statusAula === 'CONFIRMADA').length;
        this.aulasPendentes = aulas.filter(a => a.statusAula === 'SOLICITADA').length;
      }
      
      // Carrega feedbacks para aulas REALIZADAS
      aulas
        .filter(a => a. statusAula === 'REALIZADA')
        .forEach(aula => {
          this.feedbackService.buscarPorAula(aula.id).subscribe({
            next: (feedback) => {
              aula.feedback = feedback;
              console.log('✅ Feedback carregado para aula', aula.id, feedback);
            },
            error: () => {
              console. log('⚠️ Sem feedback para aula', aula.id);
            }
          });
        });
      
      this.isLoading = false;
    });
  }

  isProfessor(): boolean {
    return this.currentUser?. tipoUsuario === 'PROFESSOR';
  }

  isAluno(): boolean {
    return this.currentUser?.tipoUsuario === 'ALUNO';
  }

  confirmarAula(aulaId: number): void {
    this.aulaService.aceitarAula(aulaId).subscribe({
      next: () => {
        console.log('✅ Aula confirmada com sucesso!');
        alert('Aula confirmada com sucesso!');
        this.carregarAulas(); 
      },
      error: (err) => {
        console.error('❌ Erro ao confirmar aula:', err);
        alert('Erro ao confirmar aula.  Tente novamente.');
      }
    });
  }

  abrirModalAceitarAula(aula: Aula): void {
    this.aulaParaAceitar = aula;
    this.showLinkModal = true;
    this.linkInvalido = false;
    
    const professor = this.currentUser as Professor;
    if (professor?. linkPadraoAula && professor?. usarLinkPadrao) {
      this.linkReuniao = professor.linkPadraoAula;
      this. usarLinkPadrao = true;
    }
  }

  fecharModalLink(): void {
    this.showLinkModal = false;
    this.aulaParaAceitar = null;
    this.linkReuniao = '';
    this.usarLinkPadrao = false;
    this.linkInvalido = false;
  }

  validarLink(link: string): boolean {
    if (!link || link.trim() === '') return false;
    
    const linkLower = link.toLowerCase();
    const plataformasValidas = [
      'zoom. us',
      'meet.google.com',
      'teams. microsoft.com',
      'teams.live.com',
      'http://',
      'https://'
    ];

    return plataformasValidas.some(plataforma => linkLower.includes(plataforma));
  }

  confirmarAceitacaoComLink(): void {
    if (!this.aulaParaAceitar) return;

    const linkTrimmed = this.linkReuniao.trim();

    if (! this.validarLink(linkTrimmed)) {
      this. linkInvalido = true;
      alert('❌ Link inválido!  Use um link de Zoom, Google Meet, Microsoft Teams ou URL válida (http/https).');
      return;
    }

    this.aulaService.aceitarAulaComLink(this.aulaParaAceitar.id, linkTrimmed).subscribe({
      next: () => {
        console.log('✅ Aula aceita com link! ');
        alert('✅ Aula confirmada com sucesso!  O aluno receberá o link da reunião.');
        this.fecharModalLink();
        this.carregarAulas();
        
        if (this.usarLinkPadrao) {
          console.log('💾 Link padrão salvo para próximas aulas');
        }
      },
      error: (err) => {
        console.error('❌ Erro ao aceitar aula:', err);
        alert('Erro ao aceitar aula. Tente novamente.');
      }
    });
  }

  recusarAula(aulaId: number): void {
    if (confirm('Tem certeza que deseja recusar esta aula? ')) {
      this.aulaService.recusarAula(aulaId).subscribe({
        next: () => {
          console.log('✅ Aula recusada.');
          alert('Aula recusada.');
          this.carregarAulas(); 
        },
        error: (err) => {
          console.error('❌ Erro ao recusar aula:', err);
          alert('Erro ao recusar aula. Tente novamente.');
        }
      });
    }
  }

  cancelarAula(aulaId: number): void {
    if (confirm('Tem certeza que deseja cancelar esta aula?')) {
      this.aulaService.cancelarAula(aulaId).subscribe({
        next: () => {
          console.log('✅ Aula cancelada.');
          alert('Aula cancelada com sucesso.');
          this.carregarAulas();
        },
        error: (err) => {
          console. error('❌ Erro ao cancelar aula:', err);
          alert('Erro ao cancelar aula. Tente novamente.');
        }
      });
    }
  }

  excluirAula(aulaId: number): void {
    if (confirm('Tem certeza que deseja EXCLUIR permanentemente esta aula?  Esta ação não pode ser desfeita.')) {
      this. aulaService.excluirAula(aulaId).subscribe({
        next: () => {
          console.log('✅ Aula excluída permanentemente.');
          alert('Aula removida com sucesso.');
          this.carregarAulas();
        },
        error: (err) => {
          console.error('❌ Erro ao excluir aula:', err);
          alert('Erro ao excluir aula. Tente novamente.');
        }
      });
    }
  }

  reagendarAula(aulaId: number): void {
    alert(`Reagendar aula #${aulaId} - Funcionalidade em desenvolvimento`);
  }

  acessarAula(aula: Aula): void {
    if (! aula.linkReuniao) {
      alert('❌ Link da reunião não disponível ainda.');
      return;
    }
    window.open(aula.linkReuniao, '_blank');
    console.log('🔗 Abrindo link da aula:', aula.linkReuniao);
  }

  podeAcessarAula(aula: Aula): boolean {
    return aula.statusAula === 'CONFIRMADA' && !!aula.linkReuniao;
  }

  // ========== FEEDBACK: NOVOS MÉTODOS ==========

  abrirModalFeedback(aula: Aula): void {
    this.aulaParaFeedback = aula;
    this.feedbackComentario = '';
    this.showFeedbackModal = true;
  }

  fecharModalFeedback(): void {
    this.showFeedbackModal = false;
    this. aulaParaFeedback = null;
    this.feedbackComentario = '';
  }

  finalizarAulaComFeedback(): void {
    if (! this.aulaParaFeedback || !this.feedbackComentario.trim()) {
      alert('❌ Por favor, escreva um comentário antes de finalizar.');
      return;
    }

    const aulaId = this.aulaParaFeedback.id;

    // 1. Marcar aula como REALIZADA
    this.aulaService.concluirAula(aulaId).subscribe({
      next: () => {
        // 2. Criar feedback
        const feedbackData: Partial<Feedback> = {
          idAula: aulaId,
          idAluno: this.currentUser! .id,
          idProfessor: this.aulaParaFeedback! .idProfessor,
          comentarioPublico: this. feedbackComentario.trim(),
          nota: 5,
          recomenda: true
        };

        this.feedbackService.criarFeedback(feedbackData).subscribe({
          next: () => {
            alert('🎉 Aula finalizada com sucesso!  Obrigado pelo feedback.');
            this.fecharModalFeedback();
            this.carregarAulas();
          },
          error: (err) => {
            console. error('Erro ao enviar feedback:', err);
            alert('Aula finalizada, mas houve erro ao enviar feedback.');
            this.fecharModalFeedback();
            this.carregarAulas();
          }
        });
      },
      error: (err) => {
        console.error('Erro ao finalizar aula:', err);
        alert('❌ Erro ao finalizar aula. Tente novamente.');
      }
    });
  }

  formatarDataFeedback(data: string): string {
    if (!data) return '';
    const d = new Date(data);
    return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  }

  // ========== MÉTODOS AUXILIARES ==========

  private ordenarAulasPorStatus(aulas: Aula[]): Aula[] {
    const ordemPrioridade: { [key: string]: number } = {
      'SOLICITADA': 1,
      'CONFIRMADA': 2,
      'REALIZADA': 3,
      'RECUSADA': 4,
      'CANCELADA': 5
    };

    return aulas.sort((a, b) => {
      const prioridadeA = ordemPrioridade[a.statusAula] || 999;
      const prioridadeB = ordemPrioridade[b.statusAula] || 999;
      return prioridadeA - prioridadeB;
    });
  }

  getIniciais(nome: string): string {
    if (!nome) return '?? ';
    const names = nome.split(' ');
    if (names.length === 1) return names[0]. substring(0, 2). toUpperCase();
    return (names[0][0] + names[names.length - 1][0]). toUpperCase();
  }

  formatarData(data: string): string {
    if (!data) return 'Data não informada';
    const [ano, mes, dia] = data. split('-');
    return `${dia}/${mes}/${ano}`;
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'SOLICITADA': 'Aguardando',
      'CONFIRMADA': 'Confirmada',
      'RECUSADA': 'Recusada',
      'CANCELADA': 'Cancelada',
      'REALIZADA': 'Concluída'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    return `status-${status. toLowerCase()}`;
  }
  
  getMateriaNome(aula: Aula): string {
    return aula.materia?. nome || `Matéria ID: ${aula.idMateria}`;
  }
  
  getProfessorNome(aula: Aula): string {
    return aula.professor?.nomeCompleto || `Professor ID: ${aula.idProfessor}`;
  }
  
  getAlunoNome(aula: Aula): string {
    return aula.aluno?.nomeCompleto || `Aluno ID: ${aula.idAluno}`;
  }
}