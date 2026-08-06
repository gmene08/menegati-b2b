import { Component, computed, input, output, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import {
  STATUS_ITEM_LABEL,
  type ItemConsignado,
  type StatusItemLote,
} from '../../../../core/models/painel-revendedora-data';
import { FormsModule } from '@angular/forms';

type ColunaOrdenavel = 'codigo' | 'produto' | 'quantidade' | 'valorUnitarioCongelado' | 'status';

@Component({
  selector: 'app-minha-maleta',
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './minha-maleta.html',
  styleUrl: './minha-maleta.css',
})
export class MinhaMaleta {
  readonly itens = input.required<ItemConsignado[]>();

  readonly onMarcarVendido = output<string>();
  readonly onDesmarcarVendido = output<string>();

  protected readonly statusLabel = STATUS_ITEM_LABEL;
  protected readonly mathMin = Math.min;

  protected busca = signal('');
  protected filtroStatus = signal<StatusItemLote | 'TODOS'>('TODOS');
  protected filtroPrecoMax = signal<number | null>(null);
  protected filtroQtd = signal<number | null>(null);

  protected readonly paginaAtual = signal(1);
  protected readonly itensPorPagina = signal(5);

  protected readonly itensFiltrados = computed(() => {
    let resultado = this.itens();
    const termo = this.busca().trim().toLowerCase();
    const status = this.filtroStatus();
    const precoMax = this.filtroPrecoMax();
    const qtd = this.filtroQtd();

    if (termo) {
      resultado = resultado.filter(
        (item) =>
          item.codigo.toLowerCase().includes(termo) || item.produto.toLowerCase().includes(termo),
      );
    }
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

  protected readonly sortColuna = signal<ColunaOrdenavel | null>(null);
  protected readonly sortDirecao = signal<'asc' | 'desc'>('asc');

  protected readonly itensOrdenados = computed(() => {
    const coluna = this.sortColuna();
    const itens = this.itensFiltrados();
    if (!coluna) return itens;

    const direcao = this.sortDirecao() === 'asc' ? 1 : -1;

    return [...itens].sort((a, b) => {
      const valorA = a[coluna];
      const valorB = b[coluna];

      if (typeof valorA === 'string' && typeof valorB === 'string') {
        return valorA.localeCompare(valorB) * direcao;
      }

      return ((valorA as number) - (valorB as number)) * direcao;
    });
  });

  protected readonly totalPaginas = computed(() => {
    const total = Math.ceil(this.itensFiltrados().length / this.itensPorPagina());
    return total > 0 ? total : 1;
  });

  protected readonly itensPaginados = computed(() => {
    const inicio = (this.paginaAtual() - 1) * this.itensPorPagina();
    const fim = inicio + this.itensPorPagina();
    return this.itensOrdenados().slice(inicio, fim);
  });

  protected readonly temFiltrosAtivos = computed(
    () =>
      this.busca().trim() !== '' ||
      this.filtroStatus() !== 'TODOS' ||
      this.filtroPrecoMax() !== null ||
      this.filtroQtd() !== null,
  );

  protected aoMudarFiltro(): void {
    this.paginaAtual.set(1);
  }

  protected limparFiltros(): void {
    this.busca.set('');
    this.filtroStatus.set('TODOS');
    this.filtroPrecoMax.set(null);
    this.filtroQtd.set(null);
    this.aoMudarFiltro();
  }

  protected alternarOrdenacao(coluna: ColunaOrdenavel): void {
    if (this.sortColuna() === coluna) {
      this.sortDirecao.update((dir) => (dir === 'asc' ? 'desc' : 'asc'));
    } else {
      this.sortColuna.set(coluna);
      this.sortDirecao.set('asc');
    }
    this.paginaAtual.set(1);
  }

  protected mudarPagina(delta: number): void {
    const novaPagina = this.paginaAtual() + delta;
    if (novaPagina >= 1 && novaPagina <= this.totalPaginas()) {
      this.paginaAtual.set(novaPagina);
    }
  }

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
      default:
        return 'bg-paper-soft text-ink-soft';
    }
  }

  protected marcarComoVendido(codigo: string): void {
    this.onMarcarVendido.emit(codigo);
  }

  protected desmarcarComoVendido(codigo: string): void {
    this.onDesmarcarVendido.emit(codigo);
  }
}
