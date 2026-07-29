import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const rolesPermitidas = (route.data['roles'] as Array<string>) ?? [];

  const user = authService.user();
  const roleUsuario = user?.role;

  if (roleUsuario && rolesPermitidas.includes(roleUsuario)) {
    return true;
  }

  console.warn('Acesso Negado: Perfil sem permissão para essa rota.');

  if (roleUsuario) {
    router.navigate(['/']);
  } else {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  }

  return false;
};
