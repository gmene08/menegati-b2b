import { Component, computed, input, linkedSignal, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { ProdutoEstoque } from '../../../../core/models/admin-data';
import { AjustarQuantidadeModal, type AjustarQuantidadePayload } from './components/ajustar-quantidade-modal/ajustar-quantidade-modal';
import { NovoProdutoModal } from './components/novo-produto-modal/novo-produto-modal';

@Component({
  selector: 'app-admin-estoque',
  imports: [CurrencyPipe, FormsModule, AjustarQuantidadeModal, NovoProdutoModal],
  templateUrl: './admin-estoque.html',
  styleUrl: './admin-estoque.css',
})
export class AdminEstoque {
  readonly estoque = input<ProdutoEstoque[]>([]);

  // Cópia local editável do estoque vindo do backend: os ajustes manuais e o cadastro
  // de novo produto ainda não têm endpoint, então vivem só no cliente por enquanto.
  // linkedSignal ressincroniza sempre que o painel recarrega os dados.
  protected readonly produtos = linkedSignal(() => this.estoque());

  protected readonly busca = signal('');
  protected readonly produtoParaAjustar = signal<ProdutoEstoque | null>(null);
  protected readonly modalNovoProdutoAberto = signal(false);
  protected readonly painelCsvAberto = signal(false);

  protected readonly produtosFiltrados = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    if (!termo) return this.produtos();
    return this.produtos().filter(
      (p) => p.codigo.toLowerCase().includes(termo) || p.nome.toLowerCase().includes(termo),
    );
  });

  protected readonly totalItens = computed(() =>
    this.produtos().reduce((soma, p) => soma + p.quantidadeDisponivel, 0),
  );

  protected readonly totalSemEstoque = computed(
    () => this.produtos().filter((p) => p.quantidadeDisponivel === 0).length,
  );

  protected estoqueBadgeClasse(p: ProdutoEstoque): string {
    if (p.quantidadeDisponivel === 0) return 'bg-red-100 text-red-800';
    if (p.quantidadeDisponivel <= 5) return 'bg-amber-100 text-amber-800';
    return 'bg-emerald-100 text-emerald-800';
  }

  protected confirmarAjuste(payload: AjustarQuantidadePayload): void {
    const produto = this.produtoParaAjustar();
    if (!produto) return;
    const delta = payload.tipo === 'ENTRADA' ? payload.quantidade : -payload.quantidade;
    this.produtos.update((lista) =>
      lista.map((p) =>
        p.codigo === produto.codigo
          ? { ...p, quantidadeDisponivel: Math.max(0, p.quantidadeDisponivel + delta) }
          : p,
      ),
    );
    this.produtoParaAjustar.set(null);
  }

  protected confirmarNovoProduto(produto: ProdutoEstoque): void {
    this.produtos.update((lista) => [produto, ...lista]);
    this.modalNovoProdutoAberto.set(false);
  }
}
