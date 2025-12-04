import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
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

  // chave separada para persistir apenas a URL da foto (sobrevive ao logout)
  private readonly FOTO_KEY = 'usuarioFotoPerfil';

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

  // ------------------- Auth flows (login/register) -------------------
  register(dadosCadastro: any): void {
    const { nomeCompleto, email, password, tipoUsuario } = dadosCadastro;
    const payload = { nomeCompleto, email: email.toLowerCase(), password, tipoUsuario };
    this.http.post<Usuario>(`${environment.apiUrl}/api/auth/register`, payload)
      .subscribe({
        next: () => { alert('Cadastro criado com sucesso!'); this.router.navigate(['/auth/login']); },
        error: (err) => { console.error('Erro no cadastro:', err); alert(err.status === 409 ? 'Este email já está cadastrado!' : 'Erro ao cadastrar.'); }
      });
  }

  login(dadosLogin: any): void {
    const { email, password } = dadosLogin;
    const payload = { email, password };
    this.http.post<Usuario>(`${environment.apiUrl}/api/auth/login`, payload)
      .subscribe({
        next: (usuario) => {
          console.log('Login bem sucedido!', usuario);
          // merge + normalize foto URL
          this.refreshCurrentUserSessionWithMerge(usuario);
          this.isLoggedInSubject.next(true);
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Erro no login:', err);
          alert('Usuário ou senha inválidos');
        }
      });
  }

  // ------------------- Session helpers -------------------
  private setSession(usuario: Usuario): void {
    if (isPlatformBrowser(this.platformId)) {
      const normalized = this.normalizeUserPhotoInUserObject(usuario);
      localStorage.setItem('usuarioLogado', JSON.stringify(normalized));
      // save only when defined
      if (normalized.fotoPerfil !== undefined) this.saveUserPhotoToStorage(normalized.fotoPerfil);
    }
    this.isLoggedInSubject.next(true);
    this.currentUserSubject.next(usuario);
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      // mantemos usuarioFotoPerfil para persistência entre logins (fallback)
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

  // ------------------- Profile update / merge -------------------
  updateUserProfile(payload: any, usuarioId: number) {
    return this.http.put<Usuario>(`${this.apiUrl}/${usuarioId}`, payload).pipe(
      tap(usuarioAtualizado => {
        console.log('AuthService.updateUserProfile - Backend respondeu:', usuarioAtualizado);
        this.refreshCurrentUserSessionWithMerge(usuarioAtualizado);
      }),
      catchError(err => {
        console.error('Erro ao atualizar perfil:', err);
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
      return !!(professor.sobre && professor.valorHora && professor.valorHora > 0 && professor.materias && professor.materias.length > 0);
    }
    return true;
  }

  // ------------------- Merge + normalization -------------------
  refreshCurrentUserSession(usuario: Usuario): void {
    this.refreshCurrentUserSessionWithMerge(usuario);
  }

  private refreshCurrentUserSessionWithMerge(usuario: Usuario): void {
    const current = this.getCurrentUserFromStorage() || this.currentUserSubject.value || null;
    let merged: any;
    if (current) {
      merged = { ...current, ...(usuario || {}) };
    } else {
      merged = { ...(usuario || {}) };
    }

    // normalize and pick foto (priority: backend -> current -> stored)
    const fotoFromBackend = (usuario as any)?.fotoPerfil as string | undefined;
    const fotoFromCurrent = (current as any)?.fotoPerfil as string | undefined;
    const fotoFromStorage = this.getUserPhotoFromStorage();

    if (fotoFromBackend && fotoFromBackend !== '') {
      merged.fotoPerfil = this.normalizePhotoUrl(fotoFromBackend);
    } else if (fotoFromCurrent && fotoFromCurrent !== '') {
      merged.fotoPerfil = this.normalizePhotoUrl(fotoFromCurrent);
    } else if (fotoFromStorage && fotoFromStorage !== '') {
      merged.fotoPerfil = this.normalizePhotoUrl(fotoFromStorage);
    } else {
      // ensure we do not set null; align with Usuario type (fotoPerfil?: string)
      delete merged.fotoPerfil;
    }

    if (isPlatformBrowser(this.platformId)) {
      // keep storage consistent with Usuario type: do not serialize null, use undefined or absence
      localStorage.setItem('usuarioLogado', JSON.stringify(merged));
      // only save if defined
      const fotoToSave = (merged as any).fotoPerfil as string | undefined;
      if (fotoToSave) this.saveUserPhotoToStorage(fotoToSave);
    }

    this.currentUserSubject.next(merged as Usuario);
  }

  // ------------------- Photo persistence helpers -------------------
  private saveUserPhotoToStorage(url?: string): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      if (url && url.length > 0) {
        // save fully normalized URL
        localStorage.setItem(this.FOTO_KEY, url);
      }
    } catch (e) {
      console.warn('Could not save user photo to storage', e);
    }
  }

  private getUserPhotoFromStorage(): string | undefined {
    if (!isPlatformBrowser(this.platformId)) return undefined;
    try {
      const v = localStorage.getItem(this.FOTO_KEY);
      return v && v.length > 0 ? v : undefined;
    } catch {
      return undefined;
    }
  }

  /**
   * Método público para persistir a foto (chame após upload bem-sucedido).
   * Garante salvar na chave separada e atualizar o usuário em sessão local.
   */
  public persistUserPhoto(url?: string): void {
    if (!isPlatformBrowser(this.platformId)) return;
    const normalized = url ? this.normalizePhotoUrl(url) : undefined;
    if (normalized) this.saveUserPhotoToStorage(normalized);

    // atualiza usuarioLogado em storage/subject se houver
    const current = this.getCurrentUserFromStorage() || this.currentUserSubject.value || null;
    if (current) {
      const updated = { ...current, fotoPerfil: normalized } as any;
      // remove fotoPerfil property if undefined to keep types consistent
      if (updated.fotoPerfil === undefined) delete updated.fotoPerfil;
      localStorage.setItem('usuarioLogado', JSON.stringify(updated));
      this.currentUserSubject.next(updated as Usuario);
    }
  }

  // ------------------- URL normalization -------------------
  private normalizePhotoUrl(url?: string): string | undefined {
    if (!url) return undefined;
    const trimmed = url.trim();
    if (/^https?:\/\//i.test(trimmed)) return trimmed;
    const base = environment.apiUrl.replace(/\/$/, '');
    if (trimmed.startsWith('/')) return base + trimmed;
    return base + '/' + trimmed;
  }

  private normalizeUserPhotoInUserObject(user: any): any {
    if (!user) return user;
    const u = { ...user };
    const normalized = this.normalizePhotoUrl(u.fotoPerfil);
    if (normalized) u.fotoPerfil = normalized;
    else delete u.fotoPerfil;
    return u;
  }
}