// Dados fictícios para exemplo de layout. Os campos seguem o modelo do backend
// (Revendedor, LoteConsignacao, ItemConsignacao, DocumentoMaleta) para facilitar
// a futura integração com a API real.

export type StatusItemLote = 'ENCARREGADO' | 'MARC_VENDIDO_REV' | 'ACERTADO_VENDIDO' | 'DEVOLVIDO';

export const STATUS_ITEM_LABEL: Record<StatusItemLote, string> = {
  ENCARREGADO: 'Na maleta',
  MARC_VENDIDO_REV: 'Vendido (a confirmar)',
  ACERTADO_VENDIDO: 'Vendido (acertado)',
  DEVOLVIDO: 'Devolvido',
};

export interface RevendedoraPerfil {
  nome: string;
  codigo: string;
  metaMensal: number;
  valorDevidoAtual: number;
}

export interface ItemMaleta {
  codigo: string;
  produto: string;
  quantidade: number;
  valorUnitarioCongelado: number;
  status: StatusItemLote;
}

export interface LoteResumo {
  numeroConsignacao: string;
  status: 'ABERTO' | 'FECHADO';
  dataAbertura: string;
}

export type TipoDocumentoMaleta = 'MALETA_ENTRADA' | 'MALETA_ACERTO';

export interface AcertoHistorico {
  id: string;
  numeroConsignacao: string;
  tipoDocumento: TipoDocumentoMaleta;
  dataProcessamento: string;
  totalPecas: number;
  valorTotal: number;
}

export interface MaterialApoioItem {
  titulo: string;
  descricao: string;
  formato: string;
  icone: string;
}

export const REVENDEDORA_MOCK: RevendedoraPerfil = {
  nome: 'Eucineia',
  codigo: '519.675',
  metaMensal: 5000,
  valorDevidoAtual: 812.4,
};

export const LOTE_MOCK: LoteResumo = {
  numeroConsignacao: '2000047',
  status: 'ABERTO',
  dataAbertura: '05/06/2026',
};

export const ITENS_MALETA_MOCK: ItemMaleta[] = [
  {
    codigo: '101094',
    produto: 'Anel feminino branco com três fileiras de crystais tam. 17',
    quantidade: 2,
    valorUnitarioCongelado: 50.5,
    status: 'ENCARREGADO',
  },
  {
    codigo: '100001',
    produto: 'Brinco ágata verde gota banhado a ouro',
    quantidade: 1,
    valorUnitarioCongelado: 146.0,
    status: 'MARC_VENDIDO_REV',
  },
  {
    codigo: '105432',
    produto: 'Pulseira elo português fina prata 925',
    quantidade: 3,
    valorUnitarioCongelado: 89.9,
    status: 'ENCARREGADO',
  },
  {
    codigo: '104994',
    produto: 'Colar gargantilha veneziana banhado a ouro 18k',
    quantidade: 1,
    valorUnitarioCongelado: 210.0,
    status: 'ENCARREGADO',
  },
  {
    codigo: '108820',
    produto: 'Pingente coração cravejado zircônia',
    quantidade: 4,
    valorUnitarioCongelado: 38.9,
    status: 'ACERTADO_VENDIDO',
  },
];

export const HISTORICO_ACERTOS_MOCK: AcertoHistorico[] = [
  {
    id: 'h3',
    numeroConsignacao: '2000047',
    tipoDocumento: 'MALETA_ENTRADA',
    dataProcessamento: '05/06/2026',
    totalPecas: 45,
    valorTotal: 3450.0,
  },
  {
    id: 'h2',
    numeroConsignacao: '2000031',
    tipoDocumento: 'MALETA_ACERTO',
    dataProcessamento: '28/04/2026',
    totalPecas: 38,
    valorTotal: 2190.5,
  },
  {
    id: 'h1',
    numeroConsignacao: '2000031',
    tipoDocumento: 'MALETA_ENTRADA',
    dataProcessamento: '02/04/2026',
    totalPecas: 40,
    valorTotal: 2870.0,
  },
];

export const MATERIAL_APOIO_MOCK: MaterialApoioItem[] = [
  {
    titulo: 'Catálogo digital de peças',
    descricao: 'Fotos e descrições das coleções atuais para mostrar às suas clientes.',
    formato: 'PDF · 12 MB',
    icone:
      'M12 4.5v15m7.5-7.5h-15M19.5 12a7.5 7.5 0 11-15 0 7.5 7.5 0 0115 0z',
  },
  {
    titulo: 'Tabela de preços vigente',
    descricao: 'Valores de venda sugeridos, atualizados a cada nova carga da maleta.',
    formato: 'PDF · 850 KB',
    icone: 'M9 7h6m-6 4h6m-6 4h4M5 3.75h14A1.25 1.25 0 0120.25 5v14A1.25 1.25 0 0119 20.25H5A1.25 1.25 0 013.75 19V5A1.25 1.25 0 015 3.75z',
  },
  {
    titulo: 'Guia de cuidados com as joias',
    descricao: 'Dicas para orientar as clientes sobre conservação e brilho das peças.',
    formato: 'PDF · 1,2 MB',
    icone:
      'M12 21c-4.97-3.14-8-6.86-8-10.5A5.5 5.5 0 0112 5a5.5 5.5 0 018 5.5c0 3.64-3.03 7.36-8 10.5z',
  },
  {
    titulo: 'Roteiro de atendimento',
    descricao: 'Script com sugestões de abordagem para apresentar a maleta às clientes.',
    formato: 'PDF · 430 KB',
    icone:
      'M8 10h8M8 14h5M21 12a9 9 0 11-6.219-8.56L21 5l-1.56 4.219A8.96 8.96 0 0121 12z',
  },
];
