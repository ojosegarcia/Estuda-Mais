import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pagamento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagamento.html',
  styleUrls: ['./pagamento.css']
})
export class PagamentoComponent implements OnInit {
  aula: any = null; // objeto vindo do navigation state
  nomeAluno = '';
  cartao = '';
  validade = '';
  cvv = '';
  processing = false;
  loading = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const navState: any = window.history.state;
    if (navState && navState.aula) {
      this.aula = navState.aula;
    } else {
      this.router.navigateByUrl('/home');
    }
  }

  confirmarPagamento(): void {
    if (!this.aula) return;
    this.processing = true;
    // simula validação simples (não bloqueante)
    setTimeout(() => {
      this.processing = false;
      this.router.navigateByUrl('/minhas-aulas');
    }, 900);
  }

  cancelar(): void {
    const profId = this.aula?.professor?.id;
    if (profId) {
      this.router.navigateByUrl(`/professor-detalhe/${profId}`);
    } else {
      this.router.navigateByUrl('/busca');
    }
  }

  // utilitário para as iniciais do avatar
  getIniciais(nomeCompleto: string | undefined): string {
    if (!nomeCompleto) return '??';
    const parts = nomeCompleto.trim().split(' ').filter(p => p.length>0);
    if (parts.length === 1) return parts[0].slice(0,2).toUpperCase();
    return (parts[0][0] + parts[parts.length-1][0]).toUpperCase();
  }

  formatarData(data: string): string {
    if (!data) return 'Data não informada';
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
  }
}