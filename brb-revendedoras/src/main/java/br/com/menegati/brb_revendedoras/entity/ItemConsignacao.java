package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item_consignacao")
public class ItemConsignacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteConsignação lote;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_codigo", nullable = false)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario_congelado", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitarioCongelado; // Evita problemas se o preço do produto mudar no catálogo global

    @Enumerated(EnumType.STRING)
    @Column(name="status_item",nullable = false)
    private StatusItemLote statusItem = StatusItemLote.ENCARREGADO;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
}
