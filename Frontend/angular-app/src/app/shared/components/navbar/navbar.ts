import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnDestroy {
  currentUser: any = null;
  currentUser$!: Observable<any | null>;
  isLoggedIn$: Observable<boolean>;
  private sub?: Subscription;
  private userMenuOpen = false;

  constructor(private auth: AuthService, private router: Router) {
    this.currentUser$ = this.auth.currentUser$;
    this.isLoggedIn$ = this.auth.currentUser$.pipe(map(u => !!u));
    this.sub = this.auth.currentUser$.subscribe(u => this.currentUser = u);
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  getUserInitials(name?: string): string {
    if (!name) return '??';
    const parts = name.trim().split(' ').filter(Boolean);
    if (parts.length === 0) return '??';
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
  }

  isUserMenuOpen(): boolean {
    return this.userMenuOpen;
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }

  goToAgenda(): void {
    this.router.navigate(['/minhas-aulas']);
  }

  goToPerfil(): void {
    this.router.navigate(['/perfil']);
    this.userMenuOpen = false;
  }

  logout(): void {
    // chama logout se existir no AuthService, senão apenas limpa localStorage
    try { this.auth.logout?.(); } catch { localStorage.removeItem('usuarioLogado'); }
    this.router.navigate(['/auth/login']);
  }

  showScrollTop(): boolean { return false; }
  scrollToTop(): void { window.scrollTo({ top: 0, behavior: 'smooth' }); }
}