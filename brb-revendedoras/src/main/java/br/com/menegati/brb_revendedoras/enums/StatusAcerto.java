package br.com.menegati.brb_revendedoras.enums;

/**
 * Status de quitação de um acerto individual. Nunca é persistido: é derivado no
 * momento da leitura por alocação FIFO dos créditos da revendedora sobre os acertos.
 */
public enum StatusAcerto {
    QUITADO,
    PARCIAL,
    EM_ABERTO
}
