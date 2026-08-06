package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.FormaPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@Entity
@Table(name = "pagamento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revendedor_id", nullable = false, updatable = false)
    private Revendedor revendedor;

    @Column(nullable = false, precision = 10, scale = 2, updatable = false)
    private BigDecimal valor;

    @Column(name = "data_pagamento", nullable = false, updatable = false)
    private LocalDateTime dataPagamento;

    @PrePersist
    protected void onCreate() {
        if (this.dataPagamento == null) {
            this.dataPagamento = LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId());
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, updatable = false)
    private FormaPagamento formaPagamento;

    @Column(length = 500, updatable = false)
    private String observacao;

    @Column(name = "registrado_por", length = 100, updatable = false)
    private String registradoPor;
}
