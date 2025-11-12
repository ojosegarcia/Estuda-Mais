import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-completion-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-completion-modal.html',
  styleUrls: ['./profile-completion-modal.css']
})
export class ProfileCompletionModalComponent {
  @Output() close = new EventEmitter<void>();

  constructor(private router: Router) {}

  completeNow(): void {
    this.router.navigate(['/perfil/editar']);
    this.close.emit();
  }

  completeLater(): void {
    this.close.emit();
  }
}
