import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Professor, Disponibilidade, Aluno, Aula } from '../../shared/models';
import { ProfessorService } from '../../core/services/professor';
import { DisponibilidadeService } from '../../core/services/disponibilidade';
import { AulaService } from '../../core/services/aula';
import { AuthService } from '../../core/services/auth';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-professor-detalhe',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './professor-detalhe.html',
  styleUrls: ['./professor-detalhe.css']
})
export class ProfessorDetalheComponent implements OnInit {

  professor$: Observable<Professor | undefined> | undefined;
  professorId!: number;
  professorCarregado: Professor | undefined; // Para guardar os dados do professor
  alunoLogado: Aluno | null = null;
  
  // Molde de horários (ex: "SEGUNDA 14:00-16:00")
  horariosRecorrentes: Disponibilidade[] = [];
  
  // O que é mostrado para o aluno
  dataSelecionada: string | null = null;
  slotsFiltrados: string[] = []; // Os horários finais (ex: "14:00", "15:00")
  isLoadingSlots = false;
  
  // Propriedade para o [min] do input de data
  public today: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private professorService: ProfessorService,
    private disponibilidadeService: DisponibilidadeService,
    private aulaService: AulaService,
    private authService: AuthService
  ) {
    // Inicializa 'today' no formato "YYYY-MM-DD"
    this.today = new Date().toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.professorId = Number(this.route.snapshot.params['id']);
    const user = this.authService.getCurrentUser();
    
    if (user && user.tipoUsuario === 'ALUNO') {
      this.alunoLogado = user as Aluno;
    }

    if (this.professorId) {
      // Carrega o professor e seus horários recorrentes
      this.professor$ = this.professorService.getProfessorById(this.professorId);
      this.professor$.subscribe(prof => this.professorCarregado = prof);
      
      this.disponibilidadeService.getDisponibilidadesPorProfessor(this.professorId).subscribe(disps => {
        this.horariosRecorrentes = disps.filter(d => d.ativo); // Pega apenas horários ativos
      });
    }
  }

  // === A LÓGICA DE AGENDAMENTO ===

  // Passo 1: O aluno seleciona um dia no calendário
  onDataSelecionada(event: Event): void {
    const dataInput = event.target as HTMLInputElement;
    this.dataSelecionada = dataInput.value;
    this.isLoadingSlots = true;
    this.slotsFiltrados = []; // Limpa os slots antigos

    if (!this.dataSelecionada) {
      this.isLoadingSlots = false;
      return;
    }

    // Validação: não permitir datas no passado
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const dataSelecionadaObj = new Date(this.dataSelecionada + 'T00:00:00');
    
    if (dataSelecionadaObj < hoje) {
      alert('Não é possível agendar aulas em datas passadas.');
      this.dataSelecionada = null;
      this.isLoadingSlots = false;
      return;
    }

    console.log('📅 Data selecionada:', this.dataSelecionada);

    // 1. Descobre o dia da semana (ex: "QUINTA")
    const diaDaSemana = this.getDiaDaSemana(this.dataSelecionada);
    console.log('📆 Dia da semana:', diaDaSemana);

    // 2. Filtra o "molde" para aquele dia
    const horariosDoDia = this.horariosRecorrentes.filter(d => d.diaSemana === diaDaSemana);
    console.log('⌚ Horários recorrentes do professor para', diaDaSemana, ':', horariosDoDia);
    
    if (horariosDoDia.length === 0) {
      console.log('⚠️ Professor não trabalha neste dia da semana');
      alert(`O professor não atende às ${this.getDiaLabel(diaDaSemana)}s.`);
      this.isLoadingSlots = false;
      return; // Professor não trabalha nesse dia
    }

    // 3. Busca no 'AulaService' as aulas JÁ AGENDADAS para este dia específico
    this.aulaService.getAulasPorProfessorEmData(this.professorId, this.dataSelecionada)
      .subscribe(aulasAgendadas => {
        console.log('📚 Aulas já agendadas nesta data:', aulasAgendadas);
        
        // Filtra aulas que estão ocupando um horário
        const horariosOcupados = aulasAgendadas
          .filter(a => a.statusAula === 'CONFIRMADA' || a.statusAula === 'SOLICITADA')
          .map(a => a.horarioInicio);
        
        console.log('❌ Horários ocupados:', horariosOcupados);
        
        // 4. Gera os slots (ex: 14:00, 15:00, 16:00)
        const slotsTotais = this.gerarSlots(horariosDoDia);
        console.log('📊 Slots totais gerados:', slotsTotais);

        // 5. Filtra os slots (Remove horários já ocupados)
        this.slotsFiltrados = slotsTotais.filter(slot => 
          !horariosOcupados.includes(slot)
        );
        
        console.log('✅ Slots disponíveis:', this.slotsFiltrados);
        this.isLoadingSlots = false;
      });
  }

  // Passo 2: O aluno clica em um slot de horário vago
  agendarSlot(horario: string): void {
    if (!this.alunoLogado) {
      alert('Você precisa estar logado como aluno para agendar!');
      this.router.navigate(['/auth/login']);
      return;
    }
    if (!this.dataSelecionada || !this.professorCarregado) {
      alert('Erro: Professor ou data não selecionada.');
      return;
    }

    const confirma = confirm(
      `Confirmar agendamento para ${this.formatarData(this.dataSelecionada)} às ${horario}?`
    );
    if (!confirma) return;

    console.log('📝 Iniciando agendamento:', {
      data: this.dataSelecionada,
      horario: horario,
      professor: this.professorCarregado.nomeCompleto,
      aluno: this.alunoLogado.nomeCompleto
    });

    // Pega a matéria (lógica de TCC simplificada: pega a primeira matéria do professor)
    const materia = this.professorCarregado.materias?.[0] || { id: 0, nome: 'Indefinida' };

    // Cria a nova aula com data e hora específicas
    const novaAula: Omit<Aula, 'id' | 'status' | 'dataCriacao'> = {
      idProfessor: this.professorCarregado.id,
      idAluno: this.alunoLogado.id,
      idMateria: materia.id,
      dataAula: this.dataSelecionada,
      horarioInicio: horario,
      horarioFim: (parseInt(horario.split(':')[0]) + 1).toString().padStart(2, '0') + ':00', // Mock de 1h de duração
      valorAula: this.professorCarregado.valorHora || 0,
      aluno: this.alunoLogado,
      professor: this.professorCarregado,
      materia: materia,
      statusAula: 'SOLICITADA'
    };

    console.log('📦 Dados da aula a ser criada:', novaAula);

    // Chama o AulaService (que já faz o POST)
    this.aulaService.solicitarAula(novaAula).subscribe({
      next: (aulaCriada) => {
        console.log('✅ Aula criada com sucesso:', aulaCriada);
        alert('Solicitação de aula enviada com sucesso! Aguarde a confirmação do professor.');
        this.router.navigate(['/minhas-aulas']);
      },
      error: (err) => {
        console.error('❌ Erro ao solicitar aula:', err);
        alert('Erro ao solicitar aula. Tente novamente.');
      }
    });
  }

  // --- Funções Auxiliares ---

  // Transforma "Segunda, 14:00-16:00" em ["14:00", "15:00"]
  private gerarSlots(disponibilidades: Disponibilidade[]): string[] {
    const slots: string[] = [];
    disponibilidades.forEach(disp => {
      let horaInicio = parseInt(disp.horarioInicio.split(':')[0]);
      const horaFim = parseInt(disp.horarioFim.split(':')[0]);
      
      while(horaInicio < horaFim) {
        slots.push(`${horaInicio.toString().padStart(2, '0')}:00`);
        horaInicio++;
      }
    });
    return slots.sort(); // Ordena os horários
  }

  // Transforma "2025-11-20" em "QUINTA"
  getDiaDaSemana(dataString: string): string {
    const dias = ['DOMINGO', 'SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO'];
    const data = new Date(dataString + 'T00:00:00'); // Trata como data local
    return dias[data.getDay()];
  }

  // Formata data (ex: 2025-11-15 -> 15/11/2025)
  formatarData(data: string): string {
    if (!data) return 'Data não informada';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
  }

  // Traduz dia da semana para português
  getDiaLabel(dia: string): string {
    const labels: { [key: string]: string } = {
      'DOMINGO': 'Domingo',
      'SEGUNDA': 'Segunda-feira',
      'TERCA': 'Terça-feira',
      'QUARTA': 'Quarta-feira',
      'QUINTA': 'Quinta-feira',
      'SEXTA': 'Sexta-feira',
      'SABADO': 'Sábado'
    };
    return labels[dia] || dia;
  }

  // Pega iniciais
  getInitials(nomeCompleto: string | undefined): string {
    if (!nomeCompleto) return '??';
    const names = nomeCompleto.trim().split(' ').filter(n => n.length > 0);
    if (names.length === 0) return '??';
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }
}