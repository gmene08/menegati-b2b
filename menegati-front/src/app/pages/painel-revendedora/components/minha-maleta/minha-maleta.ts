import { Component, computed, input, output, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import {
  STATUS_ITEM_LABEL,
  type ItemConsignado,
  type StatusItemLote,
} from '../../../../core/models/painel-revendedora-data';
import { FormsModule } from '@angular/forms';
import { ColunaTabela } from '../../../../shared/components/produto-tabela/produto-tabela.tipos';
import { ProdutoTabela } from '../../../../shared/components/produto-tabela/produto-tabela';
import { CelulaTabela } from '../../../../shared/components/produto-tabela/celula-tabela';

const BADGE_CLASSES: Record<StatusItemLote, string> = {
  ENCARREGADO: 'bg-brand-light text-brand',
  MARC_VENDIDO_REV: 'bg-amber-100 text-amber-800',
  ACERTADO_VENDIDO: 'bg-emerald-100 text-emerald-800',
  DEVOLVIDO: 'bg-paper-soft text-ink-soft',
};

@Component({
  selector: 'app-minha-maleta',
  imports: [FormsModule, ProdutoTabela, CelulaTabela],
  templateUrl: './minha-maleta.html',
  styleUrl: './minha-maleta.css',
})
export class MinhaMaleta {
  readonly itens = input.required<ItemConsignado[]>();

  readonly onMarcarVendido = output<string>();
  readonly onDesmarcarVendido = output<string>();

  protected readonly colunas: ColunaTabela<ItemConsignado>[] = [
    { chave: 'codigo', titulo: 'Código', formato: 'mono', ordenavel: true, tipo: 'dados' },
    { chave: 'produto', titulo: 'Produto', formato: 'principal', ordenavel: true, tipo: 'dados' },
    { chave: 'quantidade', titulo: 'QTD.', formato: 'numero', ordenavel: true, tipo: 'dados' },
    { chave: 'valorUnitarioCongelado', titulo: 'Valor', formato: 'moeda', ordenavel: true,tipo: 'dados' },
    { chave: 'status', titulo: 'Status', formato: 'template', ordenavel: true, tipo: 'dados' },
    { chave: 'acao', titulo: 'Ação', tipo: 'livre' },
  ];

  protected busca = signal('');
  protected filtroStatus = signal<StatusItemLote | 'TODOS'>('TODOS');
  protected filtroPrecoMax = signal<number | null>(null);
  protected filtroQtd = signal<number | null>(null);

  protected readonly itensFiltrados = computed(() => {
    let resultado = this.itens();
    const status = this.filtroStatus();
    const precoMax = this.filtroPrecoMax();
    const qtd = this.filtroQtd();

    if (status !== 'TODOS') {
      resultado = resultado.filter((item) => item.status === status);
    }
    if (precoMax !== null && precoMax > 0) {
      resultado = resultado.filter((item) => item.valorUnitarioCongelado <= precoMax);
    }
    if (qtd !== null && qtd > 0) {
      resultado = resultado.filter((item) => item.quantidade === qtd);
    }

    return resultado;
  });

  protected readonly temFiltrosAtivos = computed(
    () =>
      this.busca().trim() !== '' ||
      this.filtroStatus() !== 'TODOS' ||
      this.filtroPrecoMax() !== null ||
      this.filtroQtd() !== null,
  );

  protected limparFiltros(): void {
    this.busca.set('');
    this.filtroStatus.set('TODOS');
    this.filtroPrecoMax.set(null);
    this.filtroQtd.set(null);
  }

  protected marcarComoVendido(codigo: string): void {
    this.onMarcarVendido.emit(codigo);
  }

  protected desmarcarComoVendido(codigo: string): void {
    this.onDesmarcarVendido.emit(codigo);
  }

  protected badgeClasse(status: StatusItemLote): string {
    return BADGE_CLASSES[status];
  }

  protected statusRotulo(status: StatusItemLote): string {
    return STATUS_ITEM_LABEL[status];
  }
}
