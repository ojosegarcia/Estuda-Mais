import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Usuario {
  name: string;
  email: string;
  password: string;
}

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css']
})
export class Auth implements OnInit {
  form!: FormGroup;
  isLoginMode = true;

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.setupForm();
  }

  setupForm(): void {
    this.form = this.fb.group({
      name: [''],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
  }

  getUsuarios(): Usuario[] {
    const dados = localStorage.getItem('usuario');
    return dados ? JSON.parse(dados) : [];
  }

  salvarUsuarios(lista: Usuario[]): void {
    localStorage.setItem('usuario', JSON.stringify(lista));
  }

  onSubmit(): void {
    console.log('Form submit enviado');
    if (this.form.invalid) return;

    const { name, email, password } = this.form.value;
    const usuarios = this.getUsuarios();

    if (this.isLoginMode) {
      // Login
      const usuario = usuarios.find((u: Usuario) => u.email === email && u.password === password);
      if (usuario) {
        console.log('Login bem sucedido!', usuario);
        localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
        this.router.navigate(['/home']);
      } else {
        alert('Email ou senha inválidos!');
      }
    } else {
      // Cadastro
      const usuarioJaExiste = usuarios.some((u: Usuario) => u.email === email);

      if (usuarioJaExiste) {
        alert('Este email já está cadastrado!');
        return;
      }

      const novoUsuario: Usuario = { name, email, password };
      usuarios.push(novoUsuario);
      this.salvarUsuarios(usuarios);

      alert('Cadastro criado com sucesso!');
      this.toggleMode();
      this.form.reset();
    }
  }
}
