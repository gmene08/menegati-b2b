export type PainelTabId = 'visao-geral' | 'maleta' | 'historico' | 'material';

export interface PainelTab {
  id: PainelTabId;
  label: string;
}

export const PAINEL_TABS: PainelTab[] = [
  { id: 'visao-geral', label: 'Visão Geral' },
  { id: 'maleta', label: 'A Minha Maleta' },
  { id: 'historico', label: 'Histórico de Acertos' },
  { id: 'material', label: 'Material de Apoio' },
];
