import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { MateriaService } from '../../core/services/materia';
import { DisponibilidadeService } from '../../core/services/disponibilidade';
import { AulaService } from '../../core/services/aula';
import { Professor, Usuario, Materia, Aluno, Disponibilidade } from '../../shared/models';
import { forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-perfil-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './perfil-edit.html',
  styleUrls: ['./perfil-edit.css']
})
export class PerfilEditComponent implements OnInit {

  currentUser: Usuario | null = null;
  profileForm!: FormGroup; // Formulário principal (Perfil, Matérias)
  
  // 3. NOVO FORMULÁRIO REATIVO para adicionar horários
  disponibilidadeForm!: FormGroup;

  isLoading = true;

  todasMaterias: Materia[] = [];
  materiasSelecionadas = new Set<number>(); // Controla a UI de matérias
  materiasCustomizadas: string[] = []; // Controla as tags customizadas
  
  disponibilidades: Disponibilidade[] = []; // Lista de horários já salvos
  diasDaSemana = ['SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'];
  
  // 4. REMOVEMOS o 'novaDisponibilidade = { ... }'

  // ... (opcoesEscolaridade e opcoesInteresse - sem mudanças) ...
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
    @Inject(PLATFORM_ID) private platformId: Object 
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    
    // 5. CRIA o formulário de disponibilidade (antes de carregar os dados)
    this.disponibilidadeForm = this.fb.group({
      diaSemana: ['SEGUNDA', Validators.required],
      horarioInicio: ['14:00', Validators.required],
      horarioFim: ['15:00', Validators.required]
    });

    if (this.isProfessor()) {
      // Carrega matérias e disponibilidades em paralelo
      forkJoin({
        materias: this.materiaService.getMaterias(),
        disponibilidades: this.disponibilidadeService.getDisponibilidadesPorProfessor(this.currentUser!.id)
      }).subscribe(({ materias, disponibilidades }) => {
        this.todasMaterias = materias;
        this.disponibilidades = disponibilidades.filter(d => d.ativo); // Filtra apenas ativos
        this.initForm(); // Inicia o form principal DEPOIS de ter os dados
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
      
      this.profileForm = this.fb.group({
        nomeCompleto: [professor.nomeCompleto, Validators.required],
        telefone: [professor.telefone || ''],
        sobre: [professor.sobre || '', Validators.required],
        metodologia: [professor.metodologia || ''],
        valorHora: [professor.valorHora || 0, [Validators.required, Validators.min(1)]],
        // 6. REMOVEMOS o FormArray 'materiasBase'
        // 7. REMOVEMOS o FormControl 'materiaCustomInput'
      });
      
      // Pre-popula o Set (lógica de clique) e a lista de tags (visual)
      this.materiasSelecionadas = new Set(professor.materias?.map(m => Number(m.id)) || []);
      this.materiasCustomizadas = professor.materias
        ?.filter(m => !this.todasMaterias.some(base => Number(base.id) === Number(m.id)))
        .map(m => m.nome) || [];
        
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

  // --- Lógica de Matérias (Está correta e alinhada com o HTML) ---
  
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

  // 8. O input de matéria customizada agora é TEMPLATE-DRIVEN (mais simples)
  // Ele não está mais no profileForm
  adicionarMateriaCustom(inputElement: HTMLInputElement): void {
    const nomeMateria = inputElement.value.trim();
    if (nomeMateria && !this.materiasCustomizadas.includes(nomeMateria)) {
      this.materiasCustomizadas.push(nomeMateria);
    }
    inputElement.value = ''; // Limpa o input
  }

  removerMateriaCustom(index: number): void {
    this.materiasCustomizadas.splice(index, 1);
  }

  // --- Helpers para o HTML ---
  isProfessor(): boolean { return this.currentUser?.tipoUsuario === 'PROFESSOR'; }
  isAluno(): boolean { return this.currentUser?.tipoUsuario === 'ALUNO'; }
  get f() { return this.profileForm.controls; }

  // --- Lógica de Salvar (onSubmit) ---
  onSubmit(): void {
    if (this.profileForm.invalid) {
      alert('Formulário inválido! Verifique os campos obrigatórios.');
      return;
    }
    if (!this.currentUser) return;

    if (this.isProfessor() && this.materiasSelecionadas.size === 0 && this.materiasCustomizadas.length === 0) {
      alert('Como professor, você deve selecionar ou adicionar pelo menos uma matéria.');
      return;
    }

    const formValue = this.profileForm.value;
    let materiasParaSalvar: Materia[] = [];

    // Lógica para salvar as matérias
    if (this.isProfessor()) {
      // Pega as matérias selecionadas do Set
      materiasParaSalvar = this.todasMaterias.filter(materia => 
        this.materiasSelecionadas.has(Number(materia.id))
      );
      
      // Pega as matérias customizadas (tags)
      const materiasCustom = this.materiasCustomizadas.map((nome, i) => ({
        id: new Date().getTime() + i,
        nome: nome,
        icone: '🆕'
      }));
      
      materiasParaSalvar = [...materiasParaSalvar, ...materiasCustom];
    }

    // Cria o objeto final para salvar
    const usuarioAtualizado: Usuario = { 
      ...this.currentUser, 
      ...formValue,
      materias: this.isProfessor() ? materiasParaSalvar : undefined
    };
    
    // Salva no db.json
    this.authService.updateUserProfile(usuarioAtualizado).subscribe({
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

  // 9. REMOVEMOS a função 'cancelarAulasDeMaterias'

  // --- Gestão de Disponibilidades (Agora 100% Reativa) ---

  adicionarDisponibilidade(): void {
    if (!this.currentUser || this.disponibilidadeForm.invalid) return;

    // Pega os valores do 'disponibilidadeForm'
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
        // Reseta o formulário de disponibilidade
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

  cancelar(): void {
    this.router.navigate(['/perfil']);
  }
}