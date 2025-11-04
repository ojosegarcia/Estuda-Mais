import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth';
import { map, take } from 'rxjs/operators';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn$.pipe(
    take(1), 
    map(isLoggedIn => {
      if (isLoggedIn) {
        return true; 
      } else {
        router.navigate(['/auth/login']);
        return false;
      }
    })
  );
};