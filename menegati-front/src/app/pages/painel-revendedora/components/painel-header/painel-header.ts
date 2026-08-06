import { Component, input, output, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PAINEL_TABS, type PainelTabId } from '../../painel-tabs';
import type { RevendedoraPerfil } from '../../../../core/models/painel-revendedora-data';

@Component({
  selector: 'app-painel-header',
  imports: [RouterLink],
  templateUrl: './painel-header.html',
  styleUrl: './painel-header.css',
})
export class PainelHeader {
  readonly revendedora = input.required<RevendedoraPerfil>();
  readonly abaAtiva = input.required<PainelTabId>();

  readonly abaChange = output<PainelTabId>();

  protected readonly tabs = PAINEL_TABS;
  protected readonly menuOpen = signal(false);

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }

  protected selecionarAba(id: PainelTabId): void {
    this.abaChange.emit(id);
    this.closeMenu();
  }

  protected iniciais(nome: string): string {
    return nome.charAt(0).toUpperCase();
  }
}
