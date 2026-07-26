import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Revendedora } from './pages/revendedora/revendedora';
import { PainelRevendedora } from './pages/painel-revendedora/painel-revendedora';
import { Login } from './pages/login/login';
import { RedefinirSenha } from './pages/redefinir-senha/redefinir-senha';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'revendedora', component: Revendedora },
  { path: 'painel-revendedora', component: PainelRevendedora },
  { path: 'login', component: Login},
  { path: 'redefinir-senha', component: RedefinirSenha}
];
