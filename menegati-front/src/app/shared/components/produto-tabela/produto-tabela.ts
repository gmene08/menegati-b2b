import {
  Component,
  computed,
  contentChildren,
  effect,
  input,
  model,
  signal,
  untracked,
} from '@angular/core';

import {
  type ColunaTabela,
  type AlinhamentoColuna,
  PADRAO_FORMATO_DADOS, ColunaDados, PADRAO_LIVRE
} from './produto-tabela.tipos';
import { CurrencyPipe, NgTemplateOutlet } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CelulaTabela } from './celula-tabela';

const CLASSE_TEXTO: Record<AlinhamentoColuna, string>={
  centro: "text-center",
  direita: "text-right",
  esquerda: ""

}

const CLASSE_BOTAO: Record<AlinhamentoColuna, string> = {
  esquerda: '',
  centro: 'w-full justify-center',
  direita: 'w-full justify-end',
};

@Component({
  selector: 'app-produto-tabela',
  imports: [CurrencyPipe, FormsModule, NgTemplateOutlet],
  templateUrl: './produto-tabela.html',
  styleUrl: './produto-tabela.css',
})
export class ProdutoTabela<T extends { codigo: string }> {
  protected readonly mathMin = Math.min;

  readonly linhas = input.required<T[]>();
  readonly colunas = input.required<ColunaTabela<T>[]>();

  readonly acaoTitulo = input('Ação');
  readonly larguraAcao = input('w-44');

  readonly busca = model('');
  readonly buscavel = input<boolean>(true);
  readonly buscaPlaceholder = input('Buscar...');
  readonly titulo = input<string>('');

  protected readonly sortColuna = signal<(keyof T & string) | null>(null);
  protected readonly sortDirecao = signal<'asc' | 'desc'>('asc');

  readonly paginavel = input(true);
  protected readonly paginaAtual = signal(1);
  readonly itensPorPagina = input(5);

  private readonly celulas = contentChildren(CelulaTabela);

  constructor() {
    // Filtros do pai trocam a referência de `linhas` → volta pra primeira página.
    effect(() => {
      this.linhas();
      untracked(() => this.paginaAtual.set(1));
    });
  }

  private readonly colunaDados = computed(()=>{
    return this.colunas().filter((c): c is ColunaDados<T> => this.ehDados(c));
  })

  private readonly camposBusca = computed(() =>
    this.colunaDados()
      .filter((c) => c.buscavel ?? this.padraoColuna(c).buscavel)
      .map((c) => c.chave),
  );

  protected readonly linhasFiltradas = computed(() => {
    const termo = this.busca().trim().toLowerCase();
    if (!termo || !this.buscavel()) return this.linhas();

    const campos = this.camposBusca();
    return this.linhas().filter((l) =>
      campos.some((c) =>
        String(l[c] ?? '')
          .toLowerCase()
          .includes(termo),
      ),
    );
  });

  protected readonly linhasOrdenadas = computed(() => {
    const coluna = this.sortColuna();
    const linhas = this.linhasFiltradas();

    if (!coluna) return linhas;

    const collator = Intl.Collator('pt-BR', { numeric: true, sensitivity: 'base' });

    const direcao = this.sortDirecao() === 'asc' ? 1 : -1;

    return [...linhas].sort((a, b) => {
      const va = a[coluna];
      const vb = b[coluna];

      if (typeof va === 'string' && typeof vb === 'string') {
        return collator.compare(va, vb) * direcao;
      }
      return (Number(va) - Number(vb)) * direcao;
    });
  });

  protected readonly totalDePaginas = computed(() => {
    if (!this.paginavel()) return 1;
    const total = Math.ceil(this.linhasFiltradas().length / this.itensPorPagina());
    return total > 0 ? total : 1;
  });

  /** Página efetiva: nunca sai do intervalo válido, mesmo se a lista encolher. */
  protected readonly pagina = computed(() => Math.min(this.paginaAtual(), this.totalDePaginas()));

  protected readonly linhasVisiveis = computed(() => {
    if (!this.paginavel()) return this.linhasOrdenadas();
    const inicio = (this.pagina() - 1) * this.itensPorPagina();
    return this.linhasOrdenadas().slice(inicio, inicio + this.itensPorPagina());
  });

  protected mudarPagina(delta: number): void {
    const novaPagina = this.pagina() + delta;
    if (novaPagina >= 1 && novaPagina <= this.totalDePaginas()) this.paginaAtual.set(novaPagina);
  }

  protected alternarOrdenacao(coluna: ColunaDados<T>): void {
    if (!coluna.ordenavel) return;

    if (this.sortColuna() === coluna.chave) {
      this.sortDirecao.update((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      this.sortColuna.set(coluna.chave);
      this.sortDirecao.set('asc');
    }
    this.paginaAtual.set(1);
  }

  protected valorTexto(linha: T, coluna: ColunaDados<T>): string {
    const v = linha[coluna.chave];
    return v == null ? '' : String(v);
  }

  protected valorMoeda(linha: T, coluna: ColunaDados<T>): number | null {
    const v = linha[coluna.chave];
    return typeof v === 'number' ? v : null;
  }

  protected classeCabecalho(coluna: ColunaTabela<T>): string {
    if(coluna.tipo === 'dados'){
      const p = this.padraoColuna(coluna);
      return `${CLASSE_TEXTO[p.alinhamento]} ${coluna.largura ?? p.largura}`.trim();
    }
    return `${CLASSE_TEXTO[PADRAO_LIVRE.alinhamento]} ${coluna.largura ?? PADRAO_LIVRE.largura}`;

  }

  protected classeAlinhamento(coluna: ColunaDados<T>): string {
    return CLASSE_TEXTO[this.padraoColuna(coluna).alinhamento];
  }

  protected classeBotao(coluna: ColunaDados<T>): string {
    return CLASSE_BOTAO[this.padraoColuna(coluna).alinhamento];
  }

  private padraoColuna(coluna: ColunaDados<T>) {
    return PADRAO_FORMATO_DADOS[coluna.formato ?? 'texto'];

  }

  protected ehDados(coluna: ColunaTabela<T>): coluna is ColunaDados<T>{
    return coluna.tipo === 'dados';
  }

  protected templatePara(chave: string): CelulaTabela['template'] | null {
    return this.celulas().find((c) => c.appCelula() === chave)?.template ?? null;
  }

  protected aoBuscar(): void {
    this.paginaAtual.set(1);
  }
}
