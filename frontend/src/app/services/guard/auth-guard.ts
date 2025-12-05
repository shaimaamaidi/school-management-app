import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../token/token.service';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
  const tokenService = inject(TokenService);
  if (isLoggedIn && tokenService.isRefreshTokenValid()) {
      return true;
    } else {
      return router.createUrlTree(['/login']);
    }
};
