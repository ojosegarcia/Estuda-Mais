import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap, switchMap } from 'rxjs/operators';
import { Aula, StatusAula } from '../../shared/models'; 
import { AuthService } from './auth';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AulaService {
  private apiUrl = 'http://localhost:3000/aulas';
  private isBrowser: boolean;

  // BehaviorSubject para que os componentes possam "ouvir" as mudanças nas aulas
  private aulasSubject: BehaviorSubject<Aula[]>;
  public aulas$: Observable<Aula[]>;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private authService: AuthService,
    private http: HttpClient
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    
    // Carrega o estado inicial vazio (será preenchido quando getAulasPorUsuarioLogado for chamado)
    this.aulasSubject = new BehaviorSubject<Aula[]>([]);
    this.aulas$ = this.aulasSubject.asObservable();
  }

  // --- MÉTODOS PRIVADOS ---

  // Recarrega todas as aulas do backend e atualiza o BehaviorSubject
  private recarregarAulas(): Observable<Aula[]> {
    return this.http.get<Aula[]>(this.apiUrl).pipe(
      tap(aulas => this.aulasSubject.next(aulas))
    );
  }

  // --- MÉTODOS PÚBLICOS (A API do Serviço) ---

  /** Aluno solicita uma aula */
  solicitarAula(novaAula: Omit<Aula, 'id' | 'statusAula' | 'dataCriacao'>): Observable<Aula> {
    const aulaCompleta: Aula = {
      ...novaAula,
      id: Date.now(), // json-server aceita, ou pode gerar automaticamente
      statusAula: 'SOLICITADA',
      dataCriacao: new Date().toISOString()
    };
    
    return this.http.post<Aula>(this.apiUrl, aulaCompleta).pipe(
      tap(() => this.recarregarAulas().subscribe()) // Recarrega a lista após criar
    );
  }

  /** Busca as aulas para o usuário logado (Aluno ou Professor) */
  getAulasPorUsuarioLogado(): Observable<Aula[]> {
    const usuario = this.authService.getCurrentUser();
    if (!usuario) {
      return this.http.get<Aula[]>(this.apiUrl); // Retorna todas se não houver usuário
    }
    
    let queryUrl = '';
    if (usuario.tipoUsuario === 'ALUNO') {
      queryUrl = `${this.apiUrl}?idAluno=${usuario.id}`;
    } else {
      queryUrl = `${this.apiUrl}?idProfessor=${usuario.id}`;
    }
    
    return this.http.get<Aula[]>(queryUrl).pipe(
      tap(aulas => this.aulasSubject.next(aulas)) // Atualiza o BehaviorSubject
    );
  }

  /** Atualiza o status de uma aula (usado por todos os métodos abaixo) */
  private atualizarStatusAula(aulaId: number, novoStatus: StatusAula): Observable<Aula> {
    // Busca a aula completa primeiro
    return this.http.get<Aula>(`${this.apiUrl}/${aulaId}`).pipe(
      switchMap(aula => {
        const aulaAtualizada = { ...aula, statusAula: novoStatus };
        return this.http.put<Aula>(`${this.apiUrl}/${aulaId}`, aulaAtualizada);
      }),
      tap(() => this.recarregarAulas().subscribe()) // Recarrega após atualizar
    );
  }

  /** Professor aceita uma aula */
  aceitarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'CONFIRMADA');
  }

  /** Professor recusa uma aula */
  recusarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'RECUSADA');
  }

  /** Aluno ou Professor cancela uma aula */
  cancelarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'CANCELADA');
  }

  /** Marca uma aula como realizada */
  marcarComoRealizada(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'REALIZADA');
  }

  /** Busca todas as aulas */
  getTodasAulas(): Observable<Aula[]> {
    return this.http.get<Aula[]>(this.apiUrl).pipe(
      tap(aulas => this.aulasSubject.next(aulas))
    );
  }

  /** Busca uma aula específica por ID */
  getAulaPorId(aulaId: number): Observable<Aula> {
    return this.http.get<Aula>(`${this.apiUrl}/${aulaId}`);
  }
}