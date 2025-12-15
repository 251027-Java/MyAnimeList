import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Redirect based on login history
  if (authService.hasLoggedInBefore()) {
    // User logged in before, redirect to login page
    router.navigate(['/login']);
  } else {
    // Brand new user, redirect to signup page
    router.navigate(['']);
  }
  return false;
};
