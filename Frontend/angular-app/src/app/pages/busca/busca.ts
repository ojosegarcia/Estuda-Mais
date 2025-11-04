import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Professor } from '../../shared/models';
import { ProfessorService } from '../../core/services/professor';
import { CardProfessorComponent } from '../../shared/components/card-professor/card-professor'; 

@Component({
  selector: 'app-busca',
  standalone: true,
  imports: [CommonModule, CardProfessorComponent], 
  templateUrl: './busca.html',
  styleUrls: ['./busca.css']
})
export class BuscaComponent implements OnInit {

  professores: Professor[] = [];
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private professorService: ProfessorService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const materiaId = +params['materiaId'];
      if (materiaId) {
        this.isLoading = true;
        this.carregarProfessores(materiaId);
      }
    });
  }

  carregarProfessores(id: number): void {
    this.professorService.getProfessoresPorMateria(id).subscribe(dados => {
      this.professores = dados;
      this.isLoading = false;
    });
  }
}