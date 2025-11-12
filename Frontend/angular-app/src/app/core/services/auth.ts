import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap, switchMap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Usuario, Aluno, Professor } from '../../shared/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:3000';
  
  private isLoggedInSubject: BehaviorSubject<boolean>;
  public isLoggedIn$: Observable<boolean>;
  
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser$: Observable<Usuario | null>;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient
  ) {
    const currentUser = this.getCurrentUser();
    this.isLoggedInSubject = new BehaviorSubject<boolean>(!!currentUser);
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(currentUser);
    
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  // ==================== REGISTER ====================
  register(dadosCadastro: any): Observable<Usuario> {
    const { nomeCompleto, email, password, tipoUsuario } = dadosCadastro;

    const novoUsuario: Usuario = {
      id: Date.now(),
      nomeCompleto,
      email,
      password,
      tipoUsuario,
      ativo: true,
      dataCadastro: new Date().toISOString()
    };

    // Verifica se email já existe, depois cria o usuário
    return this.http.get<Usuario[]>(`${this.apiUrl}/usuarios?email=${email}`).pipe(
      switchMap(usuarios => {
        if (usuarios.length > 0) {
          alert('Este email já está cadastrado!');
          return throwError(() => new Error('Email já cadastrado!'));
        }
        // Se não existe, cria o novo usuário
        return this.http.post<Usuario>(`${this.apiUrl}/usuarios`, novoUsuario);
      }),
      tap(() => {
        alert('Cadastro criado com sucesso!');
        this.router.navigate(['/auth/login']);
      }),
      catchError(err => {
        console.error('Erro ao cadastrar:', err);
        alert('Erro ao cadastrar usuário!');
        return throwError(() => err);
      })
    );
  }

  // ==================== LOGIN ====================
  login(dadosLogin: any): void {
    const { email, password } = dadosLogin;

    this.http.get<Usuario[]>(`${this.apiUrl}/usuarios?email=${email}&password=${password}`)
      .pipe(
        map(usuarios => {
          if (usuarios.length > 0) {
            return usuarios[0];
          }
          throw new Error('Email ou senha inválidos');
        })
      )
      .subscribe({
        next: (usuario) => {
          console.log('Login bem sucedido!', usuario);
          
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
          }
          
          this.isLoggedInSubject.next(true);
          this.currentUserSubject.next(usuario);
          
          this.router.navigate(['/home']);
        },
        error: (err) => {
          alert(err.message || 'Erro ao fazer login');
        }
      });
  }

  // ==================== LOGOUT ====================
  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('usuarioLogado');
    }
    this.isLoggedInSubject.next(false);
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  // ==================== GET CURRENT USER ====================
  getCurrentUser(): Usuario | null {
    if (isPlatformBrowser(this.platformId)) {
      const userData = localStorage.getItem('usuarioLogado');
      return userData ? JSON.parse(userData) : null;
    }
    return null;
  }

  // ==================== IS AUTHENTICATED ====================
  isAuthenticated(): boolean {
    return this.isLoggedInSubject.value;
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

  // ==================== UPDATE USER (PERFIL EDIT) ====================
  updateUser(usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/usuarios/${usuario.id}`, usuario).pipe(
      tap(updatedUser => {
        // Atualiza o localStorage
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('usuarioLogado', JSON.stringify(updatedUser));
        }
        // Atualiza o BehaviorSubject
        this.currentUserSubject.next(updatedUser);
      }),
      catchError(err => {
        alert('Erro ao atualizar perfil!');
        return throwError(() => err);
      })
    );
  }

  // ==================== REFRESH SESSION ====================
  refreshCurrentUserSession(usuario: Usuario): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
    }
    this.currentUserSubject.next(usuario);
  }
}