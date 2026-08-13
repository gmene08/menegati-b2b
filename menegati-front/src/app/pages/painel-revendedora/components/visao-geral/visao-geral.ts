import { Component, computed, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import type {
  ItemConsignado,
  LoteAtual,
  RevendedoraPerfil,
  DocumentoMaleta,
  Acerto,
  StatusItemLote,
} from '../../../../core/models/painel-revendedora-data';

@Component({
  selector: 'app-visao-geral',
  imports: [CurrencyPipe],
  templateUrl: './visao-geral.html',
  styleUrl: './visao-geral.css',
})
export class VisaoGeral {
  readonly revendedora = input.required<RevendedoraPerfil>();
  readonly lote = input.required<LoteAtual>();
  readonly itens = input.required<ItemConsignado[]>();
  readonly historico = input<DocumentoMaleta[]>([]);
  readonly acertos = input<Acerto[]>([]);

  // Dívida atual é o saldo devedor real, vindo do razão financeiro (ver CLAUDE.md
  // raiz — "custódia ≠ financeiro"). O vencimento exibido é o do acerto mais recente,
  // melhor indicador disponível hoje já que o backend ainda não expõe qual acerto
  // específico está em aberto.
  protected readonly emDia = computed(() => this.revendedora().valorDevidoAtual <= 0);

  protected readonly acertoMaisRecente = computed(() => {
    const acertos = this.acertos();
    if (acertos.length === 0) return null;
    return acertos.slice().sort((a, b) => this.parseDataBr(b.dataAcerto) - this.parseDataBr(a.dataAcerto))[0];
  });

  protected readonly vencimentoAtual = computed(() => this.acertoMaisRecente()?.dataVencimento ?? null);

  protected readonly vencido = computed(() => {
    const vencimento = this.vencimentoAtual();
    if (!vencimento || this.emDia()) return false;
    return this.parseDataBr(vencimento) < Date.now();
  });

  // As peças da maleta passam por 3 estágios: ainda não vendidas (ENCARREGADO),
  // vendidas mas ainda não confirmadas pela loja (MARC_VENDIDO_REV), e já acertadas
  // com a loja (ACERTADO_VENDIDO). Cada estágio mostra quantidade e valor próprios,
  // em vez de um único número ambíguo que misturava estágios diferentes.
  private readonly naoVendidas = computed(() =>
    this.itens().filter((item) => item.status === 'ENCARREGADO'),
  );
  protected readonly totalNaoVendidas = computed(() =>
    this.naoVendidas().reduce((soma, item) => soma + item.quantidade, 0),
  );
  protected readonly valorNaoVendidas = computed(() =>
    this.naoVendidas().reduce((soma, item) => soma + item.quantidade * item.valorUnitarioCongelado, 0),
  );

  private readonly vendidas = computed(() =>
    this.itens().filter((item) => item.status === 'MARC_VENDIDO_REV'),
  );
  protected readonly totalVendidas = computed(() =>
    this.vendidas().reduce((soma, item) => soma + item.quantidade, 0),
  );
  protected readonly valorVendidas = computed(() =>
    this.vendidas().reduce((soma, item) => soma + item.quantidade * item.valorUnitarioCongelado, 0),
  );

  private readonly acertadas = computed(() =>
    this.itens().filter((item) => item.status === 'ACERTADO_VENDIDO'),
  );
  protected readonly totalAcertadas = computed(() =>
    this.acertadas().reduce((soma, item) => soma + item.quantidade, 0),
  );
  protected readonly valorAcertadas = computed(() =>
    this.acertadas().reduce((soma, item) => soma + item.quantidade * item.valorUnitarioCongelado, 0),
  );

  protected readonly valorVendidoNoPeriodo = computed(() => this.valorVendidas() + this.valorAcertadas());

  protected readonly progressoMeta = computed(() => {
    const meta = this.revendedora().metaMensal;
    if (meta <= 0) return 0;
    return Math.min(100, Math.round((this.valorVendidoNoPeriodo() / meta) * 100));
  });

  protected readonly resumoStatus = computed(() => {
    const porStatus: Record<StatusItemLote, number> = {
      ENCARREGADO: 0,
      MARC_VENDIDO_REV: 0,
      ACERTADO_VENDIDO: 0,
      DEVOLVIDO: 0,
    };
    for (const item of this.itens()) {
      porStatus[item.status] += item.quantidade;
    }

    const total = porStatus.ENCARREGADO + porStatus.MARC_VENDIDO_REV + porStatus.ACERTADO_VENDIDO + porStatus.DEVOLVIDO;

    const definicoes: { status: StatusItemLote; label: string; cor: string }[] = [
      { status: 'ENCARREGADO', label: 'Na maleta', cor: 'bg-brand' },
      { status: 'MARC_VENDIDO_REV', label: 'Vendido (a confirmar)', cor: 'bg-amber-400' },
      { status: 'ACERTADO_VENDIDO', label: 'Acertado', cor: 'bg-emerald-500' },
      { status: 'DEVOLVIDO', label: 'Devolvido', cor: 'bg-ink-soft/30' },
    ];

    const segmentos = definicoes.map((def) => ({
      ...def,
      qtd: porStatus[def.status],
      percent: total > 0 ? (porStatus[def.status] / total) * 100 : 0,
    }));

    return { segmentos, total };
  });

  private parseDataBr(data: string): number {
    const [dia, mes, ano] = data.split('/').map(Number);
    if (!dia || !mes || !ano) return 0;
    return new Date(ano, mes - 1, dia).getTime();
  }

  protected readonly dadosGrafico = computed(() => {
    // Pega apenas os Acertos (Vendas Finalizadas), ordenados do mais antigo para o mais
    // recente, e fica só com os últimos 6 — assim o gráfico lê-se da esquerda para a direita
    // independentemente da ordem em que o histórico chega da API.
    const acertos = this.historico()
      .filter((doc) => doc.tipoDocumento === 'MALETA_ACERTO')
      .slice()
      .sort((a, b) => this.parseDataBr(a.dataProcessamento) - this.parseDataBr(b.dataProcessamento))
      .slice(-6);

    if (acertos.length === 0) return { barras: [], maxValor: 0 };

    // 2. Acha o maior valor para definir o topo do gráfico (100%)
    const maxValor = Math.max(...acertos.map((a) => a.valorTotal));

    // 3. Monta as barras com a percentagem de altura
    const barras = acertos.map((acerto) => {
      // Extrai apenas o dia e mês (ex: "05/06/2026" vira "05/06")
      const labelData = acerto.dataProcessamento.substring(0, 5);

      return {
        label: labelData,
        valor: acerto.valorTotal,
        // Calcula a altura da barra (se for o maior valor é 100%, os outros são proporcionais)
        alturaPercent: maxValor > 0 ? (acerto.valorTotal / maxValor) * 100 : 0,
      };
    });

    return { barras, maxValor };
  });
}
