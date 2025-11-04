import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Professor } from '../../shared/models';
import { ProfessorService } from '../../core/services/professor';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-professor-detalhe',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './professor-detalhe.html',
  styleUrls: ['./professor-detalhe.css']
})
export class ProfessorDetalheComponent implements OnInit {

  professor$: Observable<Professor | undefined> | undefined;

  constructor(
    private route: ActivatedRoute,
    private professorService: ProfessorService
  ) {}

  ngOnInit(): void {
    const professorId = +this.route.snapshot.params['id'];
    if (professorId) {
      this.professor$ = this.professorService.getProfessorById(professorId);
    }
  }

  getInitials(nomeCompleto: string | undefined): string {
    if (!nomeCompleto) return '??';
    const names = nomeCompleto.trim().split(' ');
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }
}