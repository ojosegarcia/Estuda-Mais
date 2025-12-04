import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { PhotoService } from '../../core/services/photo.service';
import { AulaService } from '../../core/services/aula';
import { Aluno } from '../../shared/models/alunoModel';
import { Professor } from '../../shared/models/professorModel';
import { Materia, Usuario, Aula } from '../../shared/models';
import { Observable, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css']
})
export class PerfilComponent implements OnInit, OnDestroy {

  currentUser$: Observable<Usuario | null>;
  usuario: any = null;
  private sub?: Subscription;

  selectedFile?: File;
  previewUrl?: string | null = null;
  uploading = false;
  uploadError?: string;

  showChangePassword = false;
  passwordModel = { newPassword: '', confirmPassword: '' };
  changingPassword = false;
  passwordError?: string;
  passwordSuccess?: string;

  aulasRealizadas = 0;

  constructor(
    private authService: AuthService,
    private photoService: PhotoService,
    private aulaService: AulaService,
    private router: Router
  ) {
    this. currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {
    this.sub = this. currentUser$.subscribe(u => {
      this.usuario = u;
      if (u) {
        this.carregarEstatisticas();
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  carregarEstatisticas(): void {
    this.aulaService.getAulasPorUsuarioLogado().subscribe({
      next: (aulas: Aula[]) => {
        this. aulasRealizadas = aulas.filter(a => a.statusAula === 'REALIZADA').length;
      },
      error: (err: any) => {
        console.error('Erro ao carregar estatísticas:', err);
      }
    });
  }

  isAluno(user: Usuario | null | undefined): boolean {
    return !! user && (user as any).tipoUsuario === 'ALUNO';
  }

  isProfessor(user: Usuario | null | undefined): boolean {
    return !!user && (user as any).tipoUsuario === 'PROFESSOR';
  }

  asAluno(user: Usuario | null | undefined): Aluno {
    return (user as Aluno) || null as any;
  }

  asProfessor(user: Usuario | null | undefined): Professor {
    return (user as Professor) || null as any;
  }

  getInteresseLabel(interesse: string | undefined): string {
    if (!interesse) return '';
    const labels: { [key: string]: string } = {
      'APRENDER_NOVO': '🌟 Aprender algo novo',
      'REFORCAR_CONHECIMENTO': '💪 Reforçar o que já sei',
      'PREPARAR_CONQUISTA': '🎯 Me preparar para uma conquista'
    };
    return labels[interesse] || interesse;
  }

  getUserInitials(nome: string | undefined): string {
    if (! nome) return '?? ';
    const names = nome. trim().split(' ').filter(n => n.length > 0);
    if (names.length === 0) return '??';
    if (names.length === 1) return names[0]. substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }

  editarPerfil(): void {
    this.router. navigate(['/perfil/editar']);
  }

  onFileSelected(event: any): void {
    const file: File = event?. target?.files?.[0];
    if (!file) return;

    if (! file.type?. startsWith?.('image/')) {
      this.uploadError = 'Por favor, selecione uma imagem. ';
      this.selectedFile = undefined;
      this.previewUrl = null;
      return;
    }

    const maxSizeMB = 5;
    if (file.size > maxSizeMB * 1024 * 1024) {
      this.uploadError = `Arquivo maior que ${maxSizeMB}MB.`;
      this.selectedFile = undefined;
      this.previewUrl = null;
      return;
    }

    this.selectedFile = file;
    this.uploadError = undefined;

    const reader = new FileReader();
    reader.onload = () => { this.previewUrl = reader.result as string; };
    reader.readAsDataURL(file);

    setTimeout(() => this.uploadSelected(), 180);
  }

  uploadSelected(): void {
    if (!this.selectedFile) return;
    if (!this.usuario || !this.usuario.id) {
      this.uploadError = 'Usuário não autenticado.';
      return;
    }

    this.uploading = true;
    this.uploadError = undefined;

    this.photoService. uploadPhoto(this.usuario.id, this.selectedFile).subscribe({
      next: (res: any) => {
        this.uploading = false;
        let fotoUrl: string | undefined;
        if (res?. fotoPerfil) fotoUrl = res.fotoPerfil;
        else if (res?.filePath) fotoUrl = res. filePath;
        else if (res?.path) fotoUrl = res.path;

        if (fotoUrl) {
          if (fotoUrl.startsWith('/')) {
            fotoUrl = environment.apiUrl.replace(/\/$/, '') + fotoUrl;
          } else if (!/^https?:\/\//i. test(fotoUrl)) {
            fotoUrl = environment.apiUrl.replace(/\/$/, '') + '/' + fotoUrl;
          }

          const updatedUser = { ...this.usuario, fotoPerfil: fotoUrl };
          try {
            const authSvc = this.authService as any;
            if (authSvc.refreshCurrentUserSession) {
              authSvc.refreshCurrentUserSession(updatedUser);
            }
          } catch {
            localStorage. setItem('usuarioLogado', JSON.stringify(updatedUser));
          }

          this.selectedFile = undefined;
          this. previewUrl = null;
        } else {
          this.uploadError = 'Resposta inesperada do servidor';
        }
      },
      error: (err: any) => {
        this.uploading = false;
        console.error('Erro upload foto', err);
        this.uploadError = err?.error?. error || err?.message || 'Falha ao enviar a foto';
      }
    });
  }

  changePassword(): void {
    this.passwordError = undefined;
    this.passwordSuccess = undefined;
    const { newPassword, confirmPassword } = this. passwordModel;
    if (! newPassword || newPassword.length < 6) {
      this. passwordError = 'A nova senha precisa ter pelo menos 6 caracteres.';
      return;
    }
    if (newPassword !== confirmPassword) {
      this.passwordError = 'As senhas não conferem.';
      return;
    }
    if (!this.usuario || !this.usuario.id) {
      this.passwordError = 'Usuário não autenticado.';
      return;
    }

    this.changingPassword = true;
    const payload: any = { password: newPassword };

    this.authService.updateUserProfile(payload, this.usuario.id).subscribe({
      next: () => {
        this.changingPassword = false;
        this. passwordSuccess = 'Senha atualizada com sucesso. ';
        this.passwordModel = { newPassword: '', confirmPassword: '' };
        this.showChangePassword = false;
      },
      error: (err: any) => {
        this.changingPassword = false;
        this. passwordError = err?.error?.error || err?.message || 'Erro ao alterar senha';
      }
    });
  }
}