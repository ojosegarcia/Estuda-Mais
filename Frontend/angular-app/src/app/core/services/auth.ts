import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { Usuario } from '../../shared/models/usuarioModel';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isLoggedInSubject: BehaviorSubject<boolean>;
  public isLoggedIn$: Observable<boolean>;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    let initialLoginState = false;
    if (isPlatformBrowser(this.platformId)) {
      initialLoginState = this.isAuthenticated();
    }
    this.isLoggedInSubject = new BehaviorSubject<boolean>(initialLoginState);
    this.isLoggedIn$ = this.isLoggedInSubject.asObservable();
  }

  private getUsuarios(): Usuario[] {
    if (isPlatformBrowser(this.platformId)) {
      const dados = localStorage.getItem('usuario');
      return dados ? JSON.parse(dados) : [];
    }
    return [];
  }

  private salvarUsuarios(lista: Usuario[]): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('usuario', JSON.stringify(lista));
    }
  }

  register(dadosCadastro: any): void {
    const { nomeCompleto, email, password, tipoUsuario } = dadosCadastro;
    const usuarios = this.getUsuarios();

    const usuarioJaExiste = usuarios.some((u: Usuario) => u.email === email);
    if (usuarioJaExiste) {
      alert('Este email já está cadastrado!');
      return;
    }

    const novoUsuario: Usuario = {
      id: new Date().getTime(), 
      nomeCompleto,
      email,
      password,
      tipoUsuario 
    };
    
    usuarios.push(novoUsuario);
    this.salvarUsuarios(usuarios);

    alert('Cadastro criado com sucesso!');
    this.router.navigate(['/auth/login']);
  }

  login(dadosLogin: any): void {
    const { email, password } = dadosLogin;
    const usuarios = this.getUsuarios();

    const usuario = usuarios.find((u: Usuario) => u.email === email && u.password === password);

    if (usuario) {
      console.log('Login bem sucedido!', usuario);
      
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
      }
      
      this.isLoggedInSubject.next(true);
      
      if (usuario.tipoUsuario === 'PROFESSOR') {
        this.router.navigate(['/home']); 
      } else {
        this.router.navigate(['/home']);
      }
      
    } else {
      alert('Email ou senha inválidos!');
    }
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('usuarioLogado');
    }
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/auth/login']);
  }

  getCurrentUser(): Usuario | null {
    if (isPlatformBrowser(this.platformId)) {
      const userData = localStorage.getItem('usuarioLogado');
      return userData ? JSON.parse(userData) : null;
    }
    return null;
  }

  isAuthenticated(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('usuarioLogado');
    }
    return false;
  }
}