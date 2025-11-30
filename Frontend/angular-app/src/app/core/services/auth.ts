import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Usuario, Aluno, Professor } from '../../shared/models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/api/usuarios`;
  private isLoggedInSubject: BehaviorSubject<boolean>;
  public isLoggedIn$: Observable<boolean>;
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser$: Observable<Usuario | null>;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {
    const currentUser = this.getCurrentUserFromStorage();
    this.isLoggedInSubject = new BehaviorSubject<boolean>(!!currentUser);
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(currentUser);
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  register(dadosCadastro: any): void {
    const { nomeCompleto, email, password, tipoUsuario } = dadosCadastro;

    const payload = {
      nomeCompleto,
      email: email.toLowerCase(),
      password,
      tipoUsuario
    };

    // Usar endpoint de registro do backend
    this.http.post<Usuario>(`${environment.apiUrl}/api/auth/register`, payload)
      .subscribe({
        next: (usuario) => {
          console.log('Cadastro criado com sucesso!', usuario);
          alert('Cadastro criado com sucesso!');
          this.router.navigate(['/auth/login']);
        },
        error: (err) => {
          console.error('Erro no cadastro:', err);
          if (err.status === 409) {
            alert('Este email já está cadastrado!');
          } else {
            alert('Erro ao cadastrar. Tente novamente.');
          }
        }
      });
  }

  login(dadosLogin: any): void {
    const { email, password } = dadosLogin;

    // Usar endpoint POST de auth
    const payload = { email, password };
    this.http.post<Usuario>(`${environment.apiUrl}/api/auth/login`, payload)
      .subscribe({
        next: (usuario) => {
          console.log('Login bem sucedido!', usuario);
          this.setSession(usuario);
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Erro no login:', err);
          alert('Usuário ou senha inválidos');
        }
      });
  }
  private setSession(usuario: Usuario): void {
    console.log('🔍 AuthService.setSession - Salvando na sessão:', {
      id: usuario.id,
      nomeCompleto: usuario.nomeCompleto,
      tipoUsuario: usuario.tipoUsuario,
      hasNomeCompleto: !!usuario.nomeCompleto
    });

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
      console.log('✅ localStorage atualizado');
    }
    this.isLoggedInSubject.next(true);
    this.currentUserSubject.next(usuario);
    console.log('✅ BehaviorSubjects atualizados');
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('usuarioLogado');
    }
    this.isLoggedInSubject.next(false);
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private getCurrentUserFromStorage(): Usuario | null {
    if (isPlatformBrowser(this.platformId)) {
      const userData = localStorage.getItem('usuarioLogado');
      return userData ? JSON.parse(userData) : null;
    }
    return null;
  }

  isAuthenticated(): boolean {
    return this.isLoggedInSubject.value;
  }

  updateUserProfile(payload: any, usuarioId: number): Observable<Usuario> {
    console.log('🔍 AuthService.updateUserProfile - Iniciando atualização:', {
      id: usuarioId,
      payload
    });

    return this.http.put<Usuario>(`${this.apiUrl}/${usuarioId}`, payload).pipe(
      tap(usuarioAtualizado => {
        console.log('✅ AuthService.updateUserProfile - Backend respondeu:', {
          id: usuarioAtualizado.id,
          nomeCompleto: usuarioAtualizado.nomeCompleto,
          tipoUsuario: usuarioAtualizado.tipoUsuario
        });
        this.setSession(usuarioAtualizado);
        console.log('✅ AuthService.updateUserProfile - Sessão atualizada no localStorage e BehaviorSubject');
      }),
      catchError(err => {
        console.error('❌ AuthService.updateUserProfile - Erro ao atualizar perfil:', err);
        alert('Erro ao atualizar o perfil. Tente novamente.');
        return throwError(() => err);
      })
    );
  }

  isProfileComplete(): boolean {
    const user = this.getCurrentUser();
    if (!user) return true;

    if (user.tipoUsuario === 'ALUNO') {
      const aluno = user as Aluno;
      return !!(aluno.escolaridade && aluno.interesse);
    } else if (user.tipoUsuario === 'PROFESSOR') {
      const professor = user as Professor;
      return !!(
        professor.sobre &&
        professor.valorHora &&
        professor.valorHora > 0 &&
        professor.materias &&
        professor.materias.length > 0
      );
    }

    return true;
  }
  refreshCurrentUserSession(usuario: Usuario): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
    }
    this.currentUserSubject.next(usuario);
  }
}

