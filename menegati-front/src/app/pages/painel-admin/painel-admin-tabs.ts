export type AdminTabId = 'visao-geral' | 'revendedoras' | 'estoque' | 'maletas';

export interface AdminTab {
  id: AdminTabId;
  label: string;
}

export const ADMIN_TABS: AdminTab[] = [
  { id: 'visao-geral', label: 'Visão Geral' },
  { id: 'revendedoras', label: 'Revendedoras' },
  { id: 'estoque', label: 'Estoque' },
  { id: 'maletas', label: 'Carga & Acerto' },
];
