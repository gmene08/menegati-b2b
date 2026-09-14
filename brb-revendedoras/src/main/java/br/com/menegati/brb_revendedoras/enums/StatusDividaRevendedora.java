package br.com.menegati.brb_revendedoras.enums;

import java.math.BigDecimal;

/** Situação financeira da revendedora, derivada do saldo devedor e do atraso. */
public enum StatusDividaRevendedora {
    EM_DIA,
    EM_ABERTO,
    ATRASADA;

    public static StatusDividaRevendedora de(BigDecimal saldoDevedor, int diasAtraso) {
        if (diasAtraso > 0) return ATRASADA;
        if (saldoDevedor.compareTo(BigDecimal.ZERO) > 0) return EM_ABERTO;
        return EM_DIA;
    }
}
