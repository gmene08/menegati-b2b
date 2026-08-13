export type PainelTabId =
  | 'visao-geral'
  | 'maleta'
  | 'historico-maleta'
  | 'historico-documentos'
  | 'material';

export interface PainelTab {
  id: PainelTabId;
  label: string;
}

export const PAINEL_TABS: PainelTab[] = [
  { id: 'visao-geral', label: 'Visão Geral' },
  { id: 'maleta', label: 'A Minha Maleta' },
  { id: 'historico-maleta', label: 'Histórico de Maleta' },
  { id: 'historico-documentos', label: 'Histórico de Documentos' },
  { id: 'material', label: 'Material de Apoio' },
];
