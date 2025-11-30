import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { MateriaService } from '../../core/services/materia';
import { DisponibilidadeService } from '../../core/services/disponibilidade';
import { AulaService } from '../../core/services/aula';
import { ExperienciaService } from '../../core/services/experiencia.service';
import { ConquistaService } from '../../core/services/conquista.service';
import { Professor, Usuario, Materia, Aluno, Disponibilidade, ExperienciaProfissional, Conquista } from '../../shared/models';
import { forkJoin, of } from 'rxjs';
import { ExperienciaFormModalComponent } from '../../shared/components/experiencia-form-modal/experiencia-form-modal';
import { ConquistaFormModalComponent } from '../../shared/components/conquista-form-modal/conquista-form-modal';

@Component({
  selector: 'app-perfil-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, ExperienciaFormModalComponent, ConquistaFormModalComponent],
  templateUrl: './perfil-edit.html',
  styleUrls: ['./perfil-edit.css']
})
export class PerfilEditComponent implements OnInit {

  currentUser: Usuario | null = null;
  profileForm!: FormGroup; 
  
  disponibilidadeForm!: FormGroup;

  isLoading = true;

  todasMaterias: Materia[] = [];
  materiasSelecionadas = new Set<number>(); 
  materiasCustomizadas: string[] = []; 
  
  disponibilidades: Disponibilidade[] = [];
  experiencias: ExperienciaProfissional[] = [];
  conquistas: Conquista[] = [];

  // Controle de modais
  showExperienciaModal = false;
  showConquistaModal = false;
  experienciaEditando: ExperienciaProfissional | null = null;
  conquistaEditando: Conquista | null = null;
  
  diasDaSemana = ['SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'];
  opcoesEscolaridade = [
    'Prefiro não dizer',
    'Ensino Fundamental Incompleto',
    'Ensino Fundamental Completo',
    'Ensino Médio Incompleto',
    'Ensino Médio Completo',
    'Superior Incompleto',
    'Superior Completo',
    'Pós-graduado'
  ];
  opcoesInteresse = [
    { value: 'APRENDER_NOVO', label: '🌟 Aprender algo novo' },
    { value: 'REFORCAR_CONHECIMENTO', label: '💪 Reforçar o que já sei' },
    { value: 'PREPARAR_CONQUISTA', label: '🎯 Me preparar para uma conquista' }
  ];

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private materiaService: MateriaService,
    private disponibilidadeService: DisponibilidadeService,
    private aulaService: AulaService,
    private experienciaService: ExperienciaService,
    private conquistaService: ConquistaService,
    @Inject(PLATFORM_ID) private platformId: Object 
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    
    this.disponibilidadeForm = this.fb.group({
      diaSemana: ['SEGUNDA', Validators.required],
      horarioInicio: ['14:00', Validators.required],
      horarioFim: ['15:00', Validators.required]
    });

    if (this.isProfessor()) {
      forkJoin({
        materias: this.materiaService.getMaterias(),
        disponibilidades: this.disponibilidadeService.getDisponibilidadesPorProfessor(this.currentUser!.id),
        experiencias: this.experienciaService.listar(this.currentUser!.id),
        conquistas: this.conquistaService.listar(this.currentUser!.id)
      }).subscribe(({ materias, disponibilidades, experiencias, conquistas }) => {
        this.todasMaterias = materias;
        this.disponibilidades = disponibilidades.filter(d => d.ativo);
        this.experiencias = experiencias;
        this.conquistas = conquistas;
        this.todasMaterias = materias;
        this.disponibilidades = disponibilidades.filter(d => d.ativo); 
        this.initForm(); 
        this.isLoading = false;
      });
    } else {
      this.initForm();
      this.isLoading = false;
    }
  }
  initForm(): void {
    if (!this.currentUser) return;

    if (this.isProfessor()) {
      const professor = this.currentUser as Professor;
      
      console.log('🔍 INIT FORM - Professor completo:', professor);
      console.log('🔍 INIT FORM - professor.materias:', professor.materias);
      
      this.profileForm = this.fb.group({
        nomeCompleto: [professor.nomeCompleto, Validators.required],
        telefone: [professor.telefone || ''],
        sobre: [professor.sobre || '', Validators.required],
        metodologia: [professor.metodologia || ''],
        valorHora: [professor.valorHora || 0, [Validators.required, Validators.min(1)]],
      });
      
      this.materiasSelecionadas = new Set(professor.materias?.map(m => Number(m.id)) || []);
      console.log('🔍 INIT FORM - materiasSelecionadas inicializada:', this.materiasSelecionadas);
        
    } else {
      const aluno = this.currentUser as Aluno;
      this.profileForm = this.fb.group({
        nomeCompleto: [aluno.nomeCompleto, Validators.required],
        telefone: [aluno.telefone || ''],
        escolaridade: [aluno.escolaridade || 'Prefiro não dizer'],
        interesse: [aluno.interesse || '', Validators.required]
      });
    }
  }

  isMateriaSelected(id: number): boolean {
    return this.materiasSelecionadas.has(Number(id));
  }

  toggleMateria(id: number): void {
    const numId = Number(id);
    if (this.materiasSelecionadas.has(numId)) {
      this.materiasSelecionadas.delete(numId);
    } else {
      this.materiasSelecionadas.add(numId);
    }
  }

  isProfessor(): boolean { return this.currentUser?.tipoUsuario === 'PROFESSOR'; }
  isAluno(): boolean { return this.currentUser?.tipoUsuario === 'ALUNO'; }
  get f() { return this.profileForm.controls; }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      alert('Formulário inválido! Verifique os campos obrigatórios.');
      return;
    }
    if (!this.currentUser) return;

    if (this.isProfessor() && this.materiasSelecionadas.size === 0) {
      alert('Como professor, você deve selecionar pelo menos uma matéria.');
      return;
    }

    const formValue = this.profileForm.value;

    // Prepara o payload para enviar ao backend
    const payload: any = {
      ...formValue
    };

    // Se for professor, adiciona os IDs das matérias
    if (this.isProfessor()) {
      payload.materiaIds = Array.from(this.materiasSelecionadas);
      console.log('🔍 FRONTEND - materiasSelecionadas (Set):', this.materiasSelecionadas);
      console.log('🔍 FRONTEND - materiaIds (Array):', payload.materiaIds);
      console.log('🔍 FRONTEND - Payload completo:', payload);
    }
    
    this.authService.updateUserProfile(payload, this.currentUser.id).subscribe({
      next: (usuarioSalvo) => {
        alert('Perfil salvo com sucesso!');
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        console.error('Erro ao atualizar perfil:', err);
        alert('Ocorreu um erro ao salvar seu perfil.');
      }
    });
  }

  adicionarDisponibilidade(): void {
    if (!this.currentUser || this.disponibilidadeForm.invalid) return;

    const formValue = this.disponibilidadeForm.value;

    const novaDisp: Omit<Disponibilidade, 'id'> = {
      idProfessor: this.currentUser.id,
      diaSemana: formValue.diaSemana as any,
      horarioInicio: formValue.horarioInicio,
      horarioFim: formValue.horarioFim,
      ativo: true
    };

    this.disponibilidadeService.criarDisponibilidade(novaDisp).subscribe({
      next: (dispCriada) => {
        this.disponibilidades.push(dispCriada);
        this.disponibilidadeForm.reset({
          diaSemana: 'SEGUNDA',
          horarioInicio: '14:00',
          horarioFim: '15:00'
        });
      },
      error: (err) => {
        console.error('Erro ao adicionar disponibilidade:', err);
        alert('Erro ao adicionar horário.');
      }
    });
  }

  removerDisponibilidade(index: number): void {
    const disp = this.disponibilidades[index];
    if (!disp.id) return;

    const dispAtualizada = { ...disp, ativo: false };

    this.disponibilidadeService.atualizarDisponibilidade(disp.id, dispAtualizada).subscribe({
      next: () => {
        this.disponibilidades.splice(index, 1);
      },
      error: (err) => {
        console.error('Erro ao remover disponibilidade:', err);
        alert('Erro ao remover horário.');
      }
    });
  }

  getDiaLabel(dia: string): string {
    const labels: any = {
      'SEGUNDA': 'Segunda-feira', 'TERCA': 'Terça-feira', 'QUARTA': 'Quarta-feira',
      'QUINTA': 'Quinta-feira', 'SEXTA': 'Sexta-feira', 'SABADO': 'Sábado', 'DOMINGO': 'Domingo'
    };
    return labels[dia] || dia;
  }

  // === EXPERIÊNCIAS PROFISSIONAIS ===

  abrirModalExperiencia(experiencia: ExperienciaProfissional | null = null): void {
    if (this.experiencias.length >= 5 && !experiencia) {
      alert('Você já atingiu o limite de 5 experiências profissionais.');
      return;
    }
    this.experienciaEditando = experiencia;
    this.showExperienciaModal = true;
  }

  fecharModalExperiencia(): void {
    this.showExperienciaModal = false;
    this.experienciaEditando = null;
  }

  salvarExperiencia(data: Partial<ExperienciaProfissional>): void {
    if (!this.currentUser) return;

    if (data.id) {
      // Atualizar
      this.experienciaService.atualizar(this.currentUser.id, data.id, data).subscribe({
        next: (atualizada: ExperienciaProfissional) => {
          const index = this.experiencias.findIndex(e => e.id === data.id);
          if (index !== -1) {
            this.experiencias[index] = atualizada;
          }
          this.fecharModalExperiencia();
          alert('Experiência atualizada com sucesso!');
        },
        error: (err: any) => {
          console.error('Erro ao atualizar experiência:', err);
          alert('Erro ao atualizar experiência.');
        }
      });
    } else {
      // Criar
      this.experienciaService.criar(this.currentUser.id, data).subscribe({
        next: (nova: ExperienciaProfissional) => {
          this.experiencias.unshift(nova); // Adiciona no início (mais recentes primeiro)
          this.fecharModalExperiencia();
          alert('Experiência adicionada com sucesso!');
        },
        error: (err: any) => {
          console.error('Erro ao adicionar experiência:', err);
          alert(err.error?.message || 'Erro ao adicionar experiência.');
        }
      });
    }
  }

  deletarExperiencia(id: number): void {
    if (!this.currentUser) return;
    if (!confirm('Tem certeza que deseja remover esta experiência?')) return;

    this.experienciaService.deletar(this.currentUser.id, id).subscribe({
      next: () => {
        this.experiencias = this.experiencias.filter(e => e.id !== id);
        alert('Experiência removida com sucesso!');
      },
      error: (err: any) => {
        console.error('Erro ao deletar experiência:', err);
        alert('Erro ao remover experiência.');
      }
    });
  }

  // === CONQUISTAS / CERTIFICADOS ===

  abrirModalConquista(conquista: Conquista | null = null): void {
    if (this.conquistas.length >= 5 && !conquista) {
      alert('Você já atingiu o limite de 5 conquistas/certificados.');
      return;
    }
    this.conquistaEditando = conquista;
    this.showConquistaModal = true;
  }

  fecharModalConquista(): void {
    this.showConquistaModal = false;
    this.conquistaEditando = null;
  }

  salvarConquista(data: Partial<Conquista>): void {
    if (!this.currentUser) return;

    if (data.id) {
      // Atualizar
      this.conquistaService.atualizar(this.currentUser.id, data.id, data).subscribe({
        next: (atualizada: Conquista) => {
          const index = this.conquistas.findIndex(c => c.id === data.id);
          if (index !== -1) {
            this.conquistas[index] = atualizada;
          }
          // Re-ordenar por ano (mais recentes primeiro)
          this.conquistas.sort((a, b) => b.ano - a.ano);
          this.fecharModalConquista();
          alert('Conquista atualizada com sucesso!');
        },
        error: (err: any) => {
          console.error('Erro ao atualizar conquista:', err);
          alert('Erro ao atualizar conquista.');
        }
      });
    } else {
      // Criar
      this.conquistaService.criar(this.currentUser.id, data).subscribe({
        next: (nova: Conquista) => {
          this.conquistas.push(nova);
          this.conquistas.sort((a, b) => b.ano - a.ano); // Ordenar por ano
          this.fecharModalConquista();
          alert('Conquista adicionada com sucesso!');
        },
        error: (err: any) => {
          console.error('Erro ao adicionar conquista:', err);
          alert(err.error?.message || 'Erro ao adicionar conquista.');
        }
      });
    }
  }

  deletarConquista(id: number): void {
    if (!this.currentUser) return;
    if (!confirm('Tem certeza que deseja remover esta conquista?')) return;

    this.conquistaService.deletar(this.currentUser.id, id).subscribe({
      next: () => {
        this.conquistas = this.conquistas.filter(c => c.id !== id);
        alert('Conquista removida com sucesso!');
      },
      error: (err: any) => {
        console.error('Erro ao deletar conquista:', err);
        alert('Erro ao remover conquista.');
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/perfil']);
  }
}