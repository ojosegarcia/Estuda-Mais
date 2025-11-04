import { Component, HostListener, signal, Inject, PLATFORM_ID } from '@angular/core'; 
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common'; 
import { AuthService} from '../../../core/services/auth'; 
import { Observable } from 'rxjs'; 
import { Usuario } from '../../models/usuarioModel';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent {
  isUserMenuOpen = signal(false);
  showScrollTop = signal(false);
  currentUser: Usuario | null = null;
  isBrowser: boolean; 

  isLoggedIn$: Observable<boolean>; 

  constructor(
    private router: Router,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object 
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    
    this.isLoggedIn$ = this.authService.isLoggedIn$;

    this.isLoggedIn$.subscribe((isLoggedIn) => {
      if (isLoggedIn) {
        this.currentUser = this.authService.getCurrentUser();
      } else {
        this.currentUser = null;
      }
    });
  }


  toggleUserMenu(): void {
    this.isUserMenuOpen.update(value => !value);
  }

  closeUserMenu(): void {
    this.isUserMenuOpen.set(false);
  }

  goToHome(): void {
    this.router.navigate(['/home']);
    this.closeUserMenu();
  }

  goToAgenda(): void {
    this.router.navigate(['/minhas-aulas']);
    this.closeUserMenu();
  }

  goToPerfil(): void {
    this.router.navigate(['/perfil']);
    this.closeUserMenu();
  }

  logout(): void {
    this.closeUserMenu();
    this.authService.logout();
  }

  scrollToTop(): void {
    if (this.isBrowser) {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      });
    }
  }

  getUserInitials(): string {
    if (!this.currentUser?.nomeCompleto) return 'U';
    const names = this.currentUser.nomeCompleto.split(' ');
    if (names.length >= 2) {
      return (names[0][0] + names[names.length - 1][0]).toUpperCase();
    }
    return this.currentUser.nomeCompleto[0].toUpperCase();
  }

  @HostListener('window:scroll', [])
  onWindowScroll(): void {
    if (this.isBrowser) {
      this.showScrollTop.set(window.scrollY > 300);
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (this.isBrowser) {
      const target = event.target as HTMLElement;
      const isClickInsideMenu = target.closest('.user-menu-wrapper');
      
      if (!isClickInsideMenu && this.isUserMenuOpen()) {
        this.closeUserMenu();
      }
    }
  }
}