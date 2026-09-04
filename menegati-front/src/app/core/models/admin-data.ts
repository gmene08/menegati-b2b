export type StatusRevendedora = 'EM_DIA' | 'EM_ABERTO' | 'ATRASADA';

export type TipoLancamento =
  | 'DEBITO_ACERTO'
  | 'CREDITO_PAGAMENTO'
  | 'CREDITO_BONUS'
  | 'DEBITO_CONSUMO_BONUS'
  | 'AJUSTE_CREDITO'
  | 'AJUSTE_DEBITO';

export type Carteira = 'DINHEIRO' | 'CREDITO_PECAS';

export type StatusAcertoAdmin = 'QUITADO' | 'PARCIAL' | 'EM_ABERTO';

export const TIPO_LANCAMENTO_LABEL: Record<TipoLancamento, string> = {
  DEBITO_ACERTO: 'Débito de acerto',
  CREDITO_PAGAMENTO: 'Pagamento recebido',
  CREDITO_BONUS: 'Bônus gerado',
  DEBITO_CONSUMO_BONUS: 'Consumo de bônus',
  AJUSTE_CREDITO: 'Ajuste (crédito)',
  AJUSTE_DEBITO: 'Ajuste (débito)',
};

export const STATUS_REVENDEDORA_LABEL: Record<StatusRevendedora, string> = {
  EM_DIA: 'Em dia',
  EM_ABERTO: 'Em aberto',
  ATRASADA: 'Atrasada',
};

export interface RevendedoraResumo {
  id: number;
  nome: string;
  cpf: string;
  telefone: string;
  saldoDevedor: number;
  saldoBonus: number;
  exposicaoMaleta: number;
  diasAtraso: number;
  dataUltimoAcerto: string | null;
  loteStatus: 'ABERTO' | 'FECHADO';
}

export interface LancamentoFinanceiro {
  id: number;
  data: string;
  tipo: TipoLancamento;
  carteira: Carteira;
  valor: number;
  descricao: string;
  saldoApos: number;
}

export interface AcertoAdmin {
  documentoMaleta: string;
  dataAcerto: string;
  dataVencimento: string;
  valorVendidoBruto: number;
  valorComissao: number;
  valorDevido: number;
  valorPago: number;
  status: StatusAcertoAdmin;
}

export interface ProdutoEstoque {
  codigo: string;
  nome: string;
  precoVenda: number;
  quantidadeDisponivel: number;
}

export interface ItemLancamentoManual {
  codigo: string;
  produto: string;
  quantidade: number;
  valorUnitario: number;
}

export type TipoProcessamento = 'ESTOQUE' | 'CARGA' | 'ACERTO';
export type OrigemProcessamento = 'PDF' | 'MANUAL' | 'CSV';

export interface ProcessamentoLog {
  id: number;
  tipo: TipoProcessamento;
  origem: OrigemProcessamento;
  revendedora: string | null;
  dataProcessamento: string;
  numeroDocumento: string;
  quantidadeItens: number;
  valorTotal: number;
}
