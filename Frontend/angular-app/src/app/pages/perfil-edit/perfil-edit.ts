import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { MateriaService } from '../../core/services/materia';
import { Professor, Usuario, Materia } from '../../shared/models';

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
  
  // Para o Professor: lista de matérias disponíveis
  todasMaterias: Materia[] = [];
  materiasSelecionadas: Set<number> = new Set();

  constructor(
    private authService: AuthService,
    private materiaService: MateriaService,
    private fb: FormBuilder,
    public router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    
    // Carregar matérias se for professor
    if (this.currentUser?.tipoUsuario === 'PROFESSOR') {
      this.materiaService.getMaterias().subscribe(materias => {
        this.todasMaterias = materias;
        
        // Pré-selecionar as matérias que o professor já tem
        const professor = this.currentUser as Professor;
        if (professor.materias) {
          professor.materias.forEach(m => this.materiasSelecionadas.add(m.id));
        }
      });
    }
    
    this.initForm();
  }

  initForm(): void {
    if (!this.currentUser) return;

    if (this.currentUser.tipoUsuario === 'PROFESSOR') {
      const professor = this.currentUser as Professor;
      this.profileForm = this.fb.group({
        nomeCompleto: [professor.nomeCompleto, Validators.required],
        telefone: [professor.telefone || ''],
        sobre: [professor.sobre || '', Validators.required],
        metodologia: [professor.metodologia || ''],
        valorHora: [professor.valorHora || 0, Validators.min(1)],
      });
    } else {
      // Formulário para Aluno
      this.profileForm = this.fb.group({
        nomeCompleto: [this.currentUser.nomeCompleto, Validators.required],
        telefone: [this.currentUser.telefone || ''],
        escolaridade: [(this.currentUser as any).escolaridade || '', Validators.required],
        interesse: [(this.currentUser as any).interesse || '', Validators.required]
      });
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      alert('Por favor, preencha todos os campos obrigatórios!');
      return;
    }
    
    // Validação extra para professor: verificar se selecionou ao menos 1 matéria
    if (this.currentUser?.tipoUsuario === 'PROFESSOR' && this.materiasSelecionadas.size === 0) {
      alert('Selecione pelo menos uma matéria que você ensina!');
      return;
    }
    
    console.log('Salvando perfil...', this.profileForm.value);
    
    let updatedUser = { ...this.currentUser, ...this.profileForm.value };

    // Se for professor, adicionar as matérias selecionadas
    if (this.currentUser?.tipoUsuario === 'PROFESSOR') {
      const materiasCompletas = this.todasMaterias.filter(m => 
        this.materiasSelecionadas.has(m.id)
      );
      updatedUser = { ...updatedUser, materias: materiasCompletas };
    }

    // Agora usa o AuthService para fazer o PUT na API
    this.authService.updateUser(updatedUser).subscribe({
      next: (usuario) => {
        alert('Perfil salvo com sucesso!');
        this.router.navigate(['/perfil']);
      },
      error: (err) => {
        console.error('Erro ao salvar perfil:', err);
      }
    });
  }

  // Toggle de seleção de matéria (para checkboxes)
  toggleMateria(materiaId: number): void {
    if (this.materiasSelecionadas.has(materiaId)) {
      this.materiasSelecionadas.delete(materiaId);
    } else {
      this.materiasSelecionadas.add(materiaId);
    }
  }

  // Verifica se uma matéria está selecionada
  isMateriaSelected(materiaId: number): boolean {
    return this.materiasSelecionadas.has(materiaId);
  }

  // Helper para o HTML
  get f() { return this.profileForm.controls; }

  // Helper para verificar se é professor
  isProfessor(): boolean {
    return this.currentUser?.tipoUsuario === 'PROFESSOR';
  }

  // Helper para verificar se é aluno
  isAluno(): boolean {
    return this.currentUser?.tipoUsuario === 'ALUNO';
  }
}