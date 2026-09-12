export type AlinhamentoColuna = 'esquerda' | 'centro' | 'direita';
export type FormatoColuna = 'texto' | 'principal' | 'mono' | 'moeda' | 'numero' | 'template';


export interface ColunaDados<T> {
  chave: keyof T & string;
  titulo: string;
  tipo: 'dados';

  formato?: FormatoColuna;
  largura?: string;
  buscavel?: boolean;
  ordenavel?: boolean;
}

export interface ColunaLivre{
  chave: string;
  titulo: string;
  tipo: 'livre';

  largura?: string;
}

export type ColunaTabela<T> = ColunaDados<T> | ColunaLivre;

export const PADRAO_FORMATO_DADOS: Record<
  FormatoColuna,
  {
    alinhamento: AlinhamentoColuna;
    largura: string;
    buscavel: boolean;
  }
> = {
  texto: { alinhamento: 'esquerda', largura: '', buscavel: true },
  principal: { alinhamento: 'esquerda', largura: '', buscavel: true },
  mono: { alinhamento: 'esquerda', largura: 'w-28', buscavel: true },
  numero: { alinhamento: 'centro', largura: 'w-20', buscavel: false },
  moeda: { alinhamento: 'direita', largura: 'w-36', buscavel: false },
  template: { alinhamento: 'centro', largura: 'w-40', buscavel: false },
};

export const PADRAO_LIVRE ={
  alinhamento: 'centro', largura: 'w-44'
} as const;
