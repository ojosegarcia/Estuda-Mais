import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Observable, of, BehaviorSubject, throwError } from 'rxjs';
import { map, switchMap, catchError, tap } from 'rxjs/operators';
import { Aula, StatusAula } from '../../shared/models';
import { AuthService } from './auth';
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

  private carregarAulasIniciais(): void {
    const usuario = this.authService.getCurrentUser();
    if (usuario) {
      this.getAulasPorUsuarioLogado().subscribe(aulas => {
        this.aulasSubject.next(aulas);
      });
    }
  }

  getAulasPorUsuarioLogado(): Observable<Aula[]> {
    const usuario = this.authService.getCurrentUser();
    if (!usuario) {
      return of([]);
    }

    let params = new HttpParams();

    if (usuario.tipoUsuario === 'ALUNO') {
      params = params.set('idAluno', usuario.id);
    } else {
      params = params.set('idProfessor', usuario.id);
    }
    const embeds = ['professor', 'aluno', 'materia'];
    embeds.forEach(embed => {
      params = params.append('_embed', embed);
    });

    return this.http.get<Aula[]>(this.apiUrl, { params: params }).pipe(
      map(aulas => {
        // Filtra aulas que o usuário atual já removeu
        return aulas.filter(aula => {
          if (usuario.tipoUsuario === 'ALUNO') {
            return !aula.removidoPeloAluno;
          } else {
            return !aula.removidoPeloProfessor;
          }
        });
      })
    );
  }

  getAulasPorProfessorEmData(professorId: number, data: string): Observable<Aula[]> {
    const params = new HttpParams()
      .set('idProfessor', professorId.toString())
      .set('dataAula', data);
      
    return this.http.get<Aula[]>(this.apiUrl, { params: params });
  }

  solicitarAula(aula: Omit<Aula, 'id' | 'status' | 'dataCriacao'>): Observable<Aula> {
    const aulaCompleta: Omit<Aula, 'id'> = {
      ...aula,
      statusAula: 'SOLICITADA',
      dataCriacao: new Date().toISOString()
    };

    return this.http.post<Aula>(this.apiUrl, aulaCompleta).pipe(
      tap(novaAula => {
        const aulasAtuais = this.aulasSubject.value;
        this.aulasSubject.next([...aulasAtuais, novaAula]);
      })
    );
  }

  private atualizarStatusAula(aulaId: number, novoStatus: StatusAula): Observable<Aula> {
    return this.http.patch<Aula>(`${this.apiUrl}/${aulaId}`, { statusAula: novoStatus }).pipe(
      tap(aulaAtualizada => {
        const aulasAtuais = this.aulasSubject.value;
        const index = aulasAtuais.findIndex(a => a.id === aulaId);
        if (index > -1) {
          aulasAtuais[index] = aulaAtualizada;
          this.aulasSubject.next([...aulasAtuais]);
        }
      })
    );
  }

  aceitarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'CONFIRMADA');
  }

  /**
   * Aceita uma aula e adiciona o link da reunião obrigatoriamente
   * @param aulaId ID da aula
   * @param linkReuniao Link da reunião (Zoom/Meet/Teams)
   */
  aceitarAulaComLink(aulaId: number, linkReuniao: string): Observable<Aula> {
    if (!linkReuniao || linkReuniao.trim() === '') {
      return throwError(() => new Error('Link da reunião é obrigatório para aceitar a aula'));
    }

    return this.http.patch<Aula>(`${this.apiUrl}/${aulaId}`, { 
      statusAula: 'CONFIRMADA',
      linkReuniao: linkReuniao.trim()
    }).pipe(
      tap(aulaAtualizada => {
        const aulasAtuais = this.aulasSubject.value;
        const index = aulasAtuais.findIndex(a => a.id === aulaId);
        if (index > -1) {
          aulasAtuais[index] = aulaAtualizada;
          this.aulasSubject.next([...aulasAtuais]);
        }
        console.log('✅ Aula aceita com link:', aulaAtualizada);
      }),
      catchError(err => {
        console.error('❌ Erro ao aceitar aula com link:', err);
        return throwError(() => err);
      })
    );
  }

  recusarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'RECUSADA');
  }

  cancelarAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'CANCELADA');
  }

  /**
   * Remove a aula apenas da visualização do usuário atual
   * Marca como removida pelo aluno ou professor, mas mantém no banco
   * Só deleta permanentemente quando AMBOS removerem
   */
  excluirAula(aulaId: number): Observable<void> {
    const usuario = this.authService.getCurrentUser();
    if (!usuario) {
      return throwError(() => new Error('Usuário não autenticado'));
    }

    // Primeiro, busca a aula atual para verificar o estado
    return this.http.get<Aula>(`${this.apiUrl}/${aulaId}`).pipe(
      switchMap(aula => {
        const ehProfessor = usuario.tipoUsuario === 'PROFESSOR';
        const ehAluno = usuario.tipoUsuario === 'ALUNO';

        // Verifica se o outro lado já removeu
        const alunoJaRemoveu = aula.removidoPeloAluno || false;
        const professorJaRemoveu = aula.removidoPeloProfessor || false;

        // Se AMBOS já removeram, deleta permanentemente
        if ((ehProfessor && alunoJaRemoveu) || (ehAluno && professorJaRemoveu)) {
          console.log('🗑️ Ambos removeram. Deletando permanentemente...');
          return this.http.delete<void>(`${this.apiUrl}/${aulaId}`).pipe(
            tap(() => {
              const aulasAtuais = this.aulasSubject.value;
              const novasAulas = aulasAtuais.filter(a => a.id !== aulaId);
              this.aulasSubject.next(novasAulas);
              console.log('✅ Aula excluída permanentemente do banco:', aulaId);
            })
          );
        }

        // Caso contrário, apenas marca como removido pelo usuário atual
        const atualizacao: Partial<Aula> = {};
        if (ehProfessor) {
          atualizacao.removidoPeloProfessor = true;
          console.log('👨‍🏫 Professor removeu a aula da sua visualização');
        } else if (ehAluno) {
          atualizacao.removidoPeloAluno = true;
          console.log('👨‍🎓 Aluno removeu a aula da sua visualização');
        }

        return this.http.patch<Aula>(`${this.apiUrl}/${aulaId}`, atualizacao).pipe(
          tap(() => {
            // Remove apenas da visualização local do usuário atual
            const aulasAtuais = this.aulasSubject.value;
            const novasAulas = aulasAtuais.filter(a => a.id !== aulaId);
            this.aulasSubject.next(novasAulas);
            console.log('✅ Aula removida da visualização:', aulaId);
          }),
          map(() => void 0) // Converte Observable<Aula> para Observable<void>
        );
      }),
      catchError(err => {
        console.error('❌ Erro ao excluir aula:', err);
        return throwError(() => err);
      })
    );
  }

  concluirAula(aulaId: number): Observable<Aula> {
    return this.atualizarStatusAula(aulaId, 'REALIZADA');
  }
}