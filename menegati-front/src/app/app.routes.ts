import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Revendedora } from './pages/revendedora/revendedora';
import { PainelRevendedora } from './pages/painel-revendedora/painel-revendedora';
import { Login } from './pages/login/login';
import { RedefinirSenha } from './pages/redefinir-senha/redefinir-senha';
import { roleGuard } from './core/guards/role-guard';
import { Role } from './core/models/role';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'revendedora', component: Revendedora },
  { path: 'painel-revendedora', component: PainelRevendedora, canActivate: [roleGuard], data: { roles: [Role.REVENDEDOR] } },
  { path: 'login', component: Login},
  { path: 'redefinir-senha', component: RedefinirSenha},
  { path: '**', redirectTo: '' },
];
