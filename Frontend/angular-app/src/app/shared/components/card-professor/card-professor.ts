// src/app/shared/components/card-professor/card-professor.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Professor } from '../../models';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-card-professor',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './card-professor.html',
  styleUrls: ['./card-professor.css']
})
export class CardProfessorComponent {
  @Input() professor!: Professor;

  getInitials(nomeCompleto: string | undefined): string {
    if (!nomeCompleto) return '??';
    const names = nomeCompleto.trim().split(' ');
    if (names.length === 1) return names[0].substring(0, 2).toUpperCase();
    return (names[0][0] + names[names.length - 1][0]).toUpperCase();
  }
}