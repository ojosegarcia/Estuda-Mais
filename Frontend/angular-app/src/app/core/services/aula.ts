import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Observable, of, BehaviorSubject, throwError } from 'rxjs';
import { map, switchMap, catchError, tap } from 'rxjs/operators';
import { Aula, StatusAula } from '../../shared/models';
import { AuthService } from './auth';

// 1. IMPORTE O 'HttpParams' (E O 'map' QUE VAI FALTAR DEPOIS)
import { HttpClient, HttpParams } from '@angular/common/http'; 

@Injectable({
  providedIn: 'root'
})
export class AulaService {
  private apiUrl = 'http://localhost:3000/aulas';
  private isBrowser: boolean;

  private aulasSubject: BehaviorSubject<Aula[]> = new BehaviorSubject<Aula[]>([]);
  public aulas$: Observable<Aula[]> = this.aulasSubject.asObservable();

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private authService: AuthService,
    private http: HttpClient
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    this.carregarAulasIniciais();
  }

  // --- MÉTODOS DE DADOS ---

  private carregarAulasIniciais(): void {
    const usuario = this.authService.getCurrentUser();
    if (usuario) {
      this.getAulasPorUsuarioLogado().subscribe(aulas => {
        this.aulasSubject.next(aulas);
      });
    }
  }

  // === MÉTODO CORRIGIDO ===
  getAulasPorUsuarioLogado(): Observable<Aula[]> {
    const usuario = this.authService.getCurrentUser();
    if (!usuario) {
      return of([]);
    }

    // 2. Crie uma instância de HttpParams
    let params = new HttpParams();

    // 3. Adicione o filtro de ID (o .set() já converte para string)
    if (usuario.tipoUsuario === 'ALUNO') {
      params = params.set('idAluno', usuario.id);
    } else {
      params = params.set('idProfessor', usuario.id);
    }

    // 4. Adicione os 'embeds' um por um usando .append()
    // Isso cria a URL correta: ...&_embed=professor&_embed=aluno&_embed=materia
    const embeds = ['professor', 'aluno', 'materia'];
    embeds.forEach(embed => {
      params = params.append('_embed', embed);
    });

    // 5. O 'return' agora funciona
    return this.http.get<Aula[]>(this.apiUrl, { params: params });
  }

  /**
   * Busca aulas de um professor em uma data específica.
   */
  getAulasPorProfessorEmData(professorId: number, data: string): Observable<Aula[]> {
    // 6. CORREÇÃO DE TIPO AQUI TAMBÉM (para garantir)
    // O json-server espera idProfessor=123&dataAula=2025-11-20
    const params = new HttpParams()
      .set('idProfessor', professorId.toString())
      .set('dataAula', data);
      
    return this.http.get<Aula[]>(this.apiUrl, { params: params });
  }

  // --- MÉTODOS DE AÇÃO ---

  /** (Item 9) Aluno solicita uma aula */
  solicitarAula(aula: Omit<Aula, 'id' | 'status' | 'dataCriacao'>): Observable<Aula> {
    const aulaCompleta: Omit<Aula, 'id'> = {
      ...aula,
      statusAula: 'SOLICITADA',
      dataCriacao: new Date().toISOString()
    };

    return this.http.post<Aula>(this.apiUrl, aulaCompleta).pipe(
      tap(novaAula => {
        // Atualiza o BehaviorSubject
        const aulasAtuais = this.aulasSubject.value;
        this.aulasSubject.next([...aulasAtuais, novaAula]);
      })
    );
  }

  /** Atualiza o status de uma aula (base para as ações) */
  private atualizarStatusAula(aulaId: number, novoStatus: StatusAula): Observable<Aula> {
    return this.http.patch<Aula>(`${this.apiUrl}/${aulaId}`, { statusAula: novoStatus }).pipe(
      tap(aulaAtualizada => {
        // Atualiza o BehaviorSubject
        const aulasAtuais = this.aulasSubject.value;
        const index = aulasAtuais.findIndex(a => a.id === aulaId);
        if (index > -1) {
          aulasAtuais[index] = aulaAtualizada;
          this.aulasSubject.next([...aulasAtuais]);
        }
      })
    );
  }

  /** (Item 11) Professor aceita uma aula */
  aceitarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'CONFIRMADA');
  }

  /** (Item 11) Professor recusa uma aula */
  recusarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'RECUSADA');
  }

  /** (Item 11) Aluno ou Professor cancela uma aula */
  cancelarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'CANCELADA');
  }

  /** (Item 12) Marca como realizada (para feedback) */
  concluirAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'REALIZADA');
  }
}