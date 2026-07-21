import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Revendedora } from './pages/revendedora/revendedora';
import { PainelRevendedora } from './pages/painel-revendedora/painel-revendedora';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'revendedora', component: Revendedora },
  { path: 'painel-revendedora', component: PainelRevendedora },
];
