import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginService } from './login.service';

export const loginGuard: CanActivateFn = (route, state) => {
  const loginService = inject(LoginService);
  const router = inject(Router);

  const isUsuariosRoute = state.url.startsWith('/blackcat/usuarios');
  const isDashboardRoute = state.url.startsWith('/blackcat/dashboard');

  if (!loginService.hasPermission('GESTOR') && isUsuariosRoute) {
    router.navigate(['invalid-access']);
    return false;
  }

   if (!loginService.hasPermission('GESTOR') && isDashboardRoute) {
    router.navigate(['invalid-access']);
    return false;
  }
  return true;
};
