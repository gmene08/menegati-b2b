import { Component, inject, OnInit, signal } from '@angular/core';
import { AdminHeader } from './components/admin-header/admin-header';
import { AdminDashboard } from './components/admin-dashboard/admin-dashboard';
import { AdminRevendedoras } from './components/admin-revendedoras/admin-revendedoras';
import { AdminEstoque } from './components/admin-estoque/admin-estoque';
import { AdminMaletas } from './components/admin-maletas/admin-maletas';
import type { AdminTabId } from './painel-admin-tabs';
import { AdminService } from '../../core/services/admin';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-painel-admin',
  imports: [AdminHeader, AdminDashboard, AdminRevendedoras, AdminEstoque, AdminMaletas],
  templateUrl: './painel-admin.html',
  styleUrl: './painel-admin.css',
})
export class PainelAdmin implements OnInit {
  private readonly adminService = inject(AdminService);

  protected readonly adminData = this.adminService.adminData;

  protected readonly isLoading = signal<boolean>(true);
  protected readonly errorMessage = signal<string>('');

  protected readonly abaAtiva = signal<AdminTabId>('visao-geral');

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.adminService
      .getAdminData()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        error: (error) => {
          console.error('Erro ao carregar os dados do painel admin:', error);
          this.errorMessage.set('Erro ao carregar os dados do painel.');
        },
      });
  }

  protected tentarNovamente(): void {
    this.carregarDados();
  }

  protected mudarAba(id: AdminTabId): void {
    this.abaAtiva.set(id);
  }
}
