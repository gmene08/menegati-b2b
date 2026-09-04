import { Component, signal } from '@angular/core';
import { AdminHeader } from './components/admin-header/admin-header';
import { AdminDashboard } from './components/admin-dashboard/admin-dashboard';
import { AdminRevendedoras } from './components/admin-revendedoras/admin-revendedoras';
import { AdminEstoque } from './components/admin-estoque/admin-estoque';
import { AdminMaletas } from './components/admin-maletas/admin-maletas';
import type { AdminTabId } from './painel-admin-tabs';

@Component({
  selector: 'app-painel-admin',
  imports: [AdminHeader, AdminDashboard, AdminRevendedoras, AdminEstoque, AdminMaletas],
  templateUrl: './painel-admin.html',
  styleUrl: './painel-admin.css',
})
export class PainelAdmin {
  protected readonly abaAtiva = signal<AdminTabId>('visao-geral');

  protected mudarAba(id: AdminTabId): void {
    this.abaAtiva.set(id);
  }
}
