import { Component, input, output } from '@angular/core';
import {
  STATUS_ITEM_LABEL,
  type ItemConsignado,
  type StatusItemLote,
} from '../../../../core/models/painel-revendedora-data';
import { FormsModule } from '@angular/forms';
import {
  ColunaTabela,
  FiltroTabela,
} from '../../../../shared/components/produto-tabela/produto-tabela.tipos';
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

  private readonly statusDesejadosNoFiltro: StatusItemLote[] = [
    'ENCARREGADO',
    'MARC_VENDIDO_REV',
    'ACERTADO_VENDIDO',
  ];

  protected readonly colunas: ColunaTabela<ItemConsignado>[] = [
    { chave: 'codigo', titulo: 'Código', formato: 'mono', ordenavel: true, tipo: 'dados' },
    { chave: 'produto', titulo: 'Produto', formato: 'principal', ordenavel: true, tipo: 'dados' },
    { chave: 'quantidade', titulo: 'QTD.', formato: 'numero', ordenavel: true, tipo: 'dados' },
    {
      chave: 'valorUnitarioCongelado',
      titulo: 'Valor',
      formato: 'moeda',
      ordenavel: true,
      tipo: 'dados',
    },
    { chave: 'status', titulo: 'Status', formato: 'template', ordenavel: true, tipo: 'dados' },
    { chave: 'acao', titulo: 'Ação', tipo: 'livre' },
  ];

  protected readonly filtros: FiltroTabela<ItemConsignado>[] = [
    {
      tipo: 'select',
      chave: 'status',
      rotulo: 'Todos os status',
      opcoes: this.statusDesejadosNoFiltro.map(status=> ({
        valor: status,
        rotulo: STATUS_ITEM_LABEL[status],
      })),
    },
    { tipo: 'max', chave: 'valorUnitarioCongelado', rotulo: 'Até R$', placeholder: 'Máx.' },
    { tipo: 'igual', chave: 'quantidade', rotulo: 'Qtd.', placeholder: 'Ex: 2' },
  ];

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
