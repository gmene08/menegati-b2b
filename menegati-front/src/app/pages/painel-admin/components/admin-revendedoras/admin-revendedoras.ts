import { Component, computed, input, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  STATUS_REVENDEDORA_LABEL,
  type AcertoAdmin,
  type LancamentoFinanceiro,
  type RevendedoraResumo,
  type StatusRevendedora,
} from '../../../../core/models/admin-data';
import { RevendedoraDetalhe } from './components/revendedora-detalhe/revendedora-detalhe';

type ColunaOrdenavel = 'nome' | 'saldoDevedor' | 'diasAtraso' | 'exposicaoMaleta';

@Component({
  selector: 'app-admin-revendedoras',
  imports: [CurrencyPipe, FormsModule, RevendedoraDetalhe],
  templateUrl: './admin-revendedoras.html',
  styleUrl: './admin-revendedoras.css',
})
export class AdminRevendedoras {
  readonly revendedoras = input<RevendedoraResumo[]>([]);

  protected readonly statusLabel = STATUS_REVENDEDORA_LABEL;

  protected readonly busca = signal('');
  protected readonly filtroStatus = signal<StatusRevendedora | 'TODOS'>('TODOS');
  protected readonly sortColuna = signal<ColunaOrdenavel>('saldoDevedor');
  protected readonly sortDirecao = signal<'asc' | 'desc'>('desc');

  protected readonly revendedoraSelecionadaId = signal<number | null>(null);

  protected status(r: RevendedoraResumo): StatusRevendedora {
    if (r.diasAtraso > 0) return 'ATRASADA';
    if (r.saldoDevedor > 0) return 'EM_ABERTO';
    return 'EM_DIA';
  }

  protected readonly revendedorasFiltradas = computed(() => {
    let resultado = this.revendedoras();
    const termo = this.busca().trim().toLowerCase();
    const status = this.filtroStatus();

    if (termo) {
      resultado = resultado.filter(
        (r) => r.nome.toLowerCase().includes(termo) || r.cpf.includes(termo),
      );
    }
    if (status !== 'TODOS') {
      resultado = resultado.filter((r) => this.status(r) === status);
    }

    const coluna = this.sortColuna();
    const direcao = this.sortDirecao() === 'asc' ? 1 : -1;
    return [...resultado].sort((a, b) => {
      const valorA = a[coluna];
      const valorB = b[coluna];
      if (typeof valorA === 'string' && typeof valorB === 'string') {
        return valorA.localeCompare(valorB) * direcao;
      }
      return ((valorA as number) - (valorB as number)) * direcao;
    });
  });

  protected readonly revendedoraSelecionada = computed(() => {
    const id = this.revendedoraSelecionadaId();
    if (id === null) return null;
    return this.revendedoras().find((r) => r.id === id) ?? null;
  });

  // TODO: o extrato de lançamentos e o histórico de acertos por revendedora ainda não
  // são servidos pelo GET /api/admin — virão de um endpoint próprio
  // (ex. GET /api/admin/revendedora/{id}/extrato). Até lá o detalhe abre vazio.
  protected readonly lancamentosSelecionados = signal<LancamentoFinanceiro[]>([]);
  protected readonly acertosSelecionados = signal<AcertoAdmin[]>([]);

  protected alternarOrdenacao(coluna: ColunaOrdenavel): void {
    if (this.sortColuna() === coluna) {
      this.sortDirecao.update((dir) => (dir === 'asc' ? 'desc' : 'asc'));
    } else {
      this.sortColuna.set(coluna);
      this.sortDirecao.set('desc');
    }
  }

  protected badgeClasse(r: RevendedoraResumo): string {
    switch (this.status(r)) {
      case 'EM_DIA':
        return 'bg-emerald-100 text-emerald-800';
      case 'EM_ABERTO':
        return 'bg-amber-100 text-amber-800';
      case 'ATRASADA':
        return 'bg-red-100 text-red-800';
    }
  }

  protected abrirDetalhe(r: RevendedoraResumo): void {
    this.revendedoraSelecionadaId.set(r.id);
  }

  protected fecharDetalhe(): void {
    this.revendedoraSelecionadaId.set(null);
  }
}
