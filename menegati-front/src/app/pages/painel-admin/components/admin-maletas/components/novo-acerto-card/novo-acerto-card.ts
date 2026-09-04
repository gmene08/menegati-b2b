import { Component, computed, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MOCK_REVENDEDORAS } from '../../../../admin-mock-data';
import type { ItemLancamentoManual } from '../../../../../../core/models/admin-data';

type ModoEntrada = 'PDF' | 'MANUAL';

const PERCENTUAL_COMISSAO = 40;

@Component({
  selector: 'app-novo-acerto-card',
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './novo-acerto-card.html',
  styleUrl: './novo-acerto-card.css',
})
export class NovoAcertoCard {
  protected readonly revendedoras = MOCK_REVENDEDORAS;
  protected readonly revendedoraId = signal<number | null>(null);
  protected readonly modo = signal<ModoEntrada>('PDF');
  protected readonly dataVencimento = signal<string>('');

  protected readonly itens = signal<ItemLancamentoManual[]>([
    { codigo: '', produto: '', quantidade: 1, valorUnitario: 0 },
  ]);

  protected readonly percentualComissao = PERCENTUAL_COMISSAO;

  protected readonly valorVendidoBruto = computed(() =>
    this.itens().reduce((soma, i) => soma + i.quantidade * i.valorUnitario, 0),
  );

  protected readonly valorComissao = computed(
    () => (this.valorVendidoBruto() * PERCENTUAL_COMISSAO) / 100,
  );

  protected readonly valorDevido = computed(() => this.valorVendidoBruto() - this.valorComissao());

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
