import { Component, computed, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import type { ItemMaleta, LoteResumo, RevendedoraPerfil } from '../../painel-data';

@Component({
  selector: 'app-visao-geral',
  imports: [CurrencyPipe],
  templateUrl: './visao-geral.html',
  styleUrl: './visao-geral.css',
})
export class VisaoGeral {
  readonly revendedora = input.required<RevendedoraPerfil>();
  readonly lote = input.required<LoteResumo>();
  readonly itens = input.required<ItemMaleta[]>();

  private readonly itensNaMaleta = computed(() =>
    this.itens().filter((item) => item.status === 'ENCARREGADO' || item.status === 'MARC_VENDIDO_REV'),
  );

  protected readonly totalPecasNaMaleta = computed(() =>
    this.itensNaMaleta().reduce((soma, item) => soma + item.quantidade, 0),
  );

  protected readonly valorTotalEstimado = computed(() =>
    this.itensNaMaleta().reduce((soma, item) => soma + item.quantidade * item.valorUnitarioCongelado, 0),
  );

  protected readonly pecasAguardandoAcerto = computed(() =>
    this.itens()
      .filter((item) => item.status === 'MARC_VENDIDO_REV')
      .reduce((soma, item) => soma + item.quantidade, 0),
  );

  protected readonly valorVendidoNoPeriodo = computed(() =>
    this.itens()
      .filter((item) => item.status === 'MARC_VENDIDO_REV' || item.status === 'ACERTADO_VENDIDO')
      .reduce((soma, item) => soma + item.quantidade * item.valorUnitarioCongelado, 0),
  );

  protected readonly progressoMeta = computed(() => {
    const meta = this.revendedora().metaMensal;
    if (meta <= 0) return 0;
    return Math.min(100, Math.round((this.valorVendidoNoPeriodo() / meta) * 100));
  });
}
