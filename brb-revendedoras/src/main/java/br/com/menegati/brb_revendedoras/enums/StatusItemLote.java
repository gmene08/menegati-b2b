package br.com.menegati.brb_revendedoras.enums;

public enum StatusItemLote {
    /**
     * Estado inicial: A peça foi enviada na maleta e está fisicamente
     * em posse da revendedora (Gerado pelo Arquivo 2).
     */
    ENCARREGADO,

    /**
     * Feedback visual: A revendedora clicou no botão "Vendido" no aplicativo.
     * Serve apenas para a organização dela e cálculo da previsão financeira no painel.
     * Não altera o estoque real da loja ainda.
     */
    MARC_VENDIDO_REV,

    /**
     * Venda Confirmada: O Admin subiu o Arquivo 3 (Acerto Final) e este produto
     * constava como vendido e pago no relatório do sistema legado.
     */
    ACERTADO_VENDIDO,

    /**
     * Devolução Automática: O Admin subiu o Arquivo 3 (Acerto Final) e este produto
     * NÃO constava na lista de vendidos. O sistema altera o status para DEVOLVIDO
     * e soma a quantidade de volta ao estoque global do produto.
     */
    DEVOLVIDO
}
