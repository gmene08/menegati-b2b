import { Component, computed, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MOCK_REVENDEDORAS } from '../../../../admin-mock-data';
import type { ItemLancamentoManual } from '../../../../../../core/models/admin-data';

type ModoEntrada = 'PDF' | 'MANUAL';

@Component({
  selector: 'app-nova-carga-card',
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './nova-carga-card.html',
  styleUrl: './nova-carga-card.css',
})
export class NovaCargaCard {
  protected readonly revendedoras = MOCK_REVENDEDORAS;
  protected readonly revendedoraId = signal<number | null>(null);
  protected readonly modo = signal<ModoEntrada>('PDF');

  protected readonly itens = signal<ItemLancamentoManual[]>([
    { codigo: '', produto: '', quantidade: 1, valorUnitario: 0 },
  ]);

  protected readonly valorTotal = computed(() =>
    this.itens().reduce((soma, i) => soma + i.quantidade * i.valorUnitario, 0),
  );

  protected adicionarItem(): void {
    this.itens.update((lista) => [...lista, { codigo: '', produto: '', quantidade: 1, valorUnitario: 0 }]);
  }

  protected removerItem(index: number): void {
    this.itens.update((lista) => lista.filter((_, i) => i !== index));
  }

  protected atualizarItem(index: number, campo: keyof ItemLancamentoManual, valor: string | number): void {
    this.itens.update((lista) =>
      lista.map((item, i) => (i === index ? { ...item, [campo]: valor } : item)),
    );
  }
}
