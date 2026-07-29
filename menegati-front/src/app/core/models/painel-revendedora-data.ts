export type StatusItemLote = 'ENCARREGADO' | 'MARC_VENDIDO_REV' | 'ACERTADO_VENDIDO' | 'DEVOLVIDO';
export type StatusLote = 'ABERTO' | 'FECHADO';
export type TipoDocumentoMaleta = 'MALETA_ENTRADA' | 'MALETA_ACERTO' | 'ESTOQUE_IMPORTE';

export const STATUS_ITEM_LABEL: Record<StatusItemLote, string> = {
  ENCARREGADO: 'Na maleta',
  MARC_VENDIDO_REV: 'Vendido (a confirmar)',
  ACERTADO_VENDIDO: 'Vendido (acertado)',
  DEVOLVIDO: 'Devolvido',
};

export const TIPO_DOCUMENTO_LABEL: Record<TipoDocumentoMaleta, string> = {
  MALETA_ENTRADA: 'Nova carga recebida',
  MALETA_ACERTO: 'Acerto realizado',
  ESTOQUE_IMPORTE: 'Atualização de estoque',
};

export interface ItemConsignado {
  codigo: string;
  produto: string;
  quantidade: number;
  valorUnitarioCongelado: number;
  status: StatusItemLote;
}

export interface RevendedoraPerfil {
  nome: string;
  email: string;
  metaMensal: number;
  valorDevidoAtual: number;
}

export interface LoteAtual {
  status: StatusLote;
  dataAbertura: string;
  valorTotalEstimado: number;
  valorTotalAcertado: number;
  itens: ItemConsignado[];
}

export interface DocumentoMaleta {
  numeroConsignacao: string;
  dataProcessamento: string;
  tipoDocumento: TipoDocumentoMaleta;
  valorTotal: number;
  quantidadePecas: number;
  loteId: number;
}

export interface PainelRevendedoraData {
  perfil: RevendedoraPerfil;
  loteAtual: LoteAtual;
  historicoDocumentos: DocumentoMaleta[];
}
