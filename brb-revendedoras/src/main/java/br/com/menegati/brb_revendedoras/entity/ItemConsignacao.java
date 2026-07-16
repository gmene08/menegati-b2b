package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item_consignacao")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemConsignacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteConsignacao lote;

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

    @Column(name = "documento_entrada", length = 50)
    private String documentoEntrada; // Guarda o número da Consignação que adicionou a peça

    @Column(name = "documento_acerto", length = 50)
    private String documentoAcerto; // Guarda o número do Acerto quando a peça for vendida/devolvida

}
