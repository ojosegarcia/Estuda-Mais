import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, catchError, tap, switchMap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http'; // 1. IMPORTE O HTTPCLIENT
import { Usuario, Aluno, Professor } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:3000/usuarios'; // URL da API
  private isLoggedInSubject: BehaviorSubject<boolean>;
  public isLoggedIn$: Observable<boolean>;
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser$: Observable<Usuario | null>;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient // 2. INJETE O HTTPCLIENT
  ) {
    const currentUser = this.getCurrentUserFromStorage();
    this.isLoggedInSubject = new BehaviorSubject<boolean>(!!currentUser);
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(currentUser);
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  // --- MÉTODOS DE REGISTRO E LOGIN (AGORA USAM HTTP) ---

  register(dadosCadastro: any): void {
    const { nomeCompleto, email, password, tipoUsuario } = dadosCadastro;

    const novoUsuario: Usuario = {
      id: new Date().getTime(), // json-server aceita isso
      nomeCompleto,
      email: email.toLowerCase(),
      password,
      tipoUsuario,
      ativo: true,
      dataCadastro: new Date().toISOString(),
      ...(tipoUsuario === 'PROFESSOR' && { aprovado: true })
    };
    
    // VERIFICA SE O EMAIL JÁ EXISTE ANTES DE CRIAR
    this.http.get<Usuario[]>(`${this.apiUrl}?email=${email.toLowerCase()}`).pipe(
      switchMap(usuarios => {
        if (usuarios.length > 0) {
          alert('Este email já está cadastrado!');
          return throwError(() => new Error('Email já existe'));
        }
        // Email não existe, faz o POST
        return this.http.post<Usuario>(this.apiUrl, novoUsuario);
      })
    ).subscribe({
      next: () => {
        alert('Cadastro criado com sucesso!');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        if (err.message !== 'Email já existe') {
          alert('Erro ao cadastrar. Tente novamente.');
        }
      }
    });
  }

  login(dadosLogin: any): void {
    const { email, password } = dadosLogin;

    this.http.get<Usuario[]>(`${this.apiUrl}?email=${email}&password=${password}`)
      .pipe(
        map(usuarios => {
          if (usuarios.length > 0) return usuarios[0];
          throw new Error('Usuário ou senha inválidos');
        })
      )
      .subscribe({
        next: (usuario) => {
          console.log('Login bem sucedido!', usuario);
          this.setSession(usuario); // Salva a sessão
          this.router.navigate(['/home']);
        },
        error: (err) => alert(err.message)
      });
  }

  // --- MÉTODOS DE SESSÃO (localStorage) ---
  
  private setSession(usuario: Usuario): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
    }
    this.isLoggedInSubject.next(true);
    this.currentUserSubject.next(usuario);
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

  // --- MÉTODO DE ATUALIZAÇÃO DE PERFIL (ONBOARDING) ---
  // O PerfilEditComponent vai chamar este método
  
  updateUserProfile(usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${usuario.id}`, usuario).pipe(
      tap(usuarioAtualizado => {
        // Atualiza a sessão local com os novos dados
        this.setSession(usuarioAtualizado);
      }),
      catchError(err => {
        alert('Erro ao atualizar o perfil. Tente novamente.');
        return throwError(() => err);
      })
    );
  }

  // ==================== IS PROFILE COMPLETE ====================
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

  // ==================== REFRESH SESSION ====================
  refreshCurrentUserSession(usuario: Usuario): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
    }
    this.currentUserSubject.next(usuario);
  }
}

