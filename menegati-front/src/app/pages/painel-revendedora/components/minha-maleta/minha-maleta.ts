import { Component, computed, input, output, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { STATUS_ITEM_LABEL, type ItemMaleta, type StatusItemLote } from '../../painel-data';

@Component({
  selector: 'app-minha-maleta',
  imports: [CurrencyPipe],
  templateUrl: './minha-maleta.html',
  styleUrl: './minha-maleta.css',
})
export class MinhaMaleta {
  readonly itens = input.required<ItemMaleta[]>();

  readonly vendido = output<string>();

  protected readonly statusLabel = STATUS_ITEM_LABEL;
  protected readonly busca = signal('');

  protected readonly itensFiltrados = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    if (!termo) return this.itens();

    return this.itens().filter(
      (item) => item.codigo.toLowerCase().includes(termo) || item.produto.toLowerCase().includes(termo),
    );
  });

  protected badgeClasse(status: StatusItemLote): string {
    switch (status) {
      case 'ENCARREGADO':
        return 'bg-brand-light text-brand';
      case 'MARC_VENDIDO_REV':
        return 'bg-amber-100 text-amber-800';
      case 'ACERTADO_VENDIDO':
        return 'bg-emerald-100 text-emerald-800';
      case 'DEVOLVIDO':
        return 'bg-paper-soft text-ink-soft';
    }
  }

  protected marcarComoVendido(codigo: string): void {
    this.vendido.emit(codigo);
  }
}
