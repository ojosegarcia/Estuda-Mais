import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
// 1. Importe FormArray e FormControl
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { MateriaService } from '../../core/services/materia';
import { AulaService } from '../../core/services/aula';
import { Professor, Usuario, Materia, Aluno } from '../../shared/models';
import { forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-perfil-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil-edit.html',
  styleUrls: ['./perfil-edit.css']
})
export class PerfilEditComponent implements OnInit {

  currentUser: Usuario | null = null;
  profileForm!: FormGroup;
  isLoading = true;

  todasMaterias: Materia[] = [];
  materiasSelecionadas = new Set<number>();
  materiasCustomizadas: string[] = []; // Para as tags de matérias customizadas

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
    private aulaService: AulaService,
    // 2. Injete o PLATFORM_ID (necessário para o onSubmit)
    @Inject(PLATFORM_ID) private platformId: Object 
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    
    if (this.isProfessor()) {
      this.materiaService.getMaterias().subscribe(materias => {
        this.todasMaterias = materias;
        this.initForm(); // Chama o initForm DEPOIS de ter as matérias
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
      
      // 3. CORREÇÃO: O Formulário do Professor agora inclui TODOS os campos do HTML
      this.profileForm = this.fb.group({
        nomeCompleto: [professor.nomeCompleto, Validators.required],
        telefone: [professor.telefone || ''],
        sobre: [professor.sobre || '', Validators.required],
        metodologia: [professor.metodologia || ''],
        valorHora: [professor.valorHora || 0, [Validators.required, Validators.min(1)]],
        
        // 4. CORREÇÃO: Cria o FormArray para os checkboxes
        materiasBase: this.fb.array(
          this.todasMaterias.map(materia => 
            this.fb.control(
              professor.materias?.some(m => Number(m.id) === Number(materia.id)) || false
            )
          )
        ),
        // 5. CORREÇÃO: Cria o FormControl para o input de nova matéria
        materiaCustomInput: [''] 
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

  // --- Lógica de Matérias do Professor ---
  
  // Helper para acessar o FormArray no HTML (caso precise)
  get materiasBaseFormArray() {
    return this.profileForm.get('materiasBase') as FormArray;
  }

  isMateriaSelected(id: number): boolean {
    // A lógica de clique (Set) é separada do FormArray, o que é ok
    return this.materiasSelecionadas.has(Number(id));
  }

  toggleMateria(id: number): void {
    const numId = Number(id);
    const formControl = this.materiasBaseFormArray.at(
      this.todasMaterias.findIndex(m => Number(m.id) === numId)
    );

    if (this.materiasSelecionadas.has(numId)) {
      this.materiasSelecionadas.delete(numId);
      formControl?.setValue(false); // Desmarca o checkbox no formulário
    } else {
      this.materiasSelecionadas.add(numId);
      formControl?.setValue(true); // Marca o checkbox no formulário
    }
  }

  adicionarMateriaCustom(): void {
    const nomeMateria = this.profileForm.get('materiaCustomInput')?.value.trim();
    if (nomeMateria && !this.materiasCustomizadas.includes(nomeMateria)) {
      this.materiasCustomizadas.push(nomeMateria);
    }
    this.profileForm.get('materiaCustomInput')?.reset();
  }

  removerMateriaCustom(index: number): void {
    this.materiasCustomizadas.splice(index, 1);
  }

  // --- Helpers para o HTML ---
  isProfessor(): boolean { return this.currentUser?.tipoUsuario === 'PROFESSOR'; }
  isAluno(): boolean { return this.currentUser?.tipoUsuario === 'ALUNO'; }
  get f() { return this.profileForm.controls; }

  // --- Lógica de Salvar ---
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

    if (this.isProfessor()) {
      // Pega as matérias selecionadas do Set (fonte da verdade)
      materiasParaSalvar = this.todasMaterias.filter(materia => 
        this.materiasSelecionadas.has(Number(materia.id))
      );
      
      // Pega as matérias customizadas (tags) - DESABILITADO mas mantém lógica
      const materiasCustom = this.materiasCustomizadas.map((nome, i) => ({
        id: new Date().getTime() + i,
        nome: nome,
        icone: '🆕'
      }));
      
      materiasParaSalvar = [...materiasParaSalvar, ...materiasCustom];

      // Cancela aulas de matérias removidas
      const professor = this.currentUser as Professor;
      const materiasAntigas = professor.materias || [];
      const materiaIdsNovos = new Set(materiasParaSalvar.map(m => Number(m.id)));
      const materiasRemovidas = materiasAntigas.filter(m => !materiaIdsNovos.has(Number(m.id)));

      if (materiasRemovidas.length > 0) {
        this.cancelarAulasDeMaterias(materiasRemovidas.map(m => Number(m.id)), Number(professor.id));
      }
    }

    // Monta o objeto final para salvar
    const usuarioAtualizado: Usuario = { 
      ...this.currentUser, 
      ...formValue,
      nomeCompleto: formValue.nomeCompleto?.trim(), // Remove espaços extras
      materias: this.isProfessor() ? materiasParaSalvar : undefined
    };

    // Remove os campos de controle do formulário antes de salvar
    delete (usuarioAtualizado as any).materiasBase;
    delete (usuarioAtualizado as any).materiaCustomInput;
    
    // Atualiza a sessão local
    if (isPlatformBrowser(this.platformId)) {
      this.authService.refreshCurrentUserSession(usuarioAtualizado);
    }

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

  // Cancela aulas pendentes/confirmadas de matérias removidas
  private cancelarAulasDeMaterias(materiaIds: number[], professorId: number): void {
    this.aulaService.getTodasAulas().subscribe(aulas => {
      const aulasParaCancelar = aulas.filter(aula => 
        Number(aula.idProfessor) === Number(professorId) && 
        materiaIds.includes(Number(aula.idMateria)) &&
        (aula.statusAula === 'SOLICITADA' || aula.statusAula === 'CONFIRMADA')
      );

      if (aulasParaCancelar.length > 0) {
        const cancelamentos = aulasParaCancelar.map(aula => 
          this.aulaService.cancelarAula(Number(aula.id))
        );
        forkJoin(cancelamentos).subscribe(() => {
          console.log(`${aulasParaCancelar.length} aula(s) cancelada(s) devido à remoção de matérias.`);
        });
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/perfil']);
  }
}