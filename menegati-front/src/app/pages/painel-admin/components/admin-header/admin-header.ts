import { Component, input, output, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ADMIN_TABS, type AdminTabId } from '../../painel-admin-tabs';

@Component({
  selector: 'app-admin-header',
  imports: [RouterLink],
  templateUrl: './admin-header.html',
  styleUrl: './admin-header.css',
})
export class AdminHeader {
  readonly nomeAdmin = input<string>('Administradora');
  readonly abaAtiva = input.required<AdminTabId>();

  readonly abaChange = output<AdminTabId>();

  protected readonly tabs = ADMIN_TABS;
  protected readonly menuOpen = signal(false);

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }

  protected selecionarAba(id: AdminTabId): void {
    this.abaChange.emit(id);
    this.closeMenu();
  }

  protected iniciais(nome: string): string {
    return nome.charAt(0).toUpperCase();
  }
}
