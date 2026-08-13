package br.com.menegati.brb_revendedoras.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carga_consignacao")
public class CargaConsignacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "revendedor_id", nullable = false)
    private Revendedor revendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false, updatable = false)
    private LoteConsignacao lote;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId());

    @Column(name = "valor_total_estimado", precision = 10, scale = 2)
    private BigDecimal valorTotalEstimado = BigDecimal.ZERO;

    @Column(name = "quantidade_pecas")
    private Integer quantidadePecas;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_maleta_id", updatable = false)
    private DocumentoMaleta documentoMaleta;

    @OneToMany(mappedBy = "carga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarga> itens = new ArrayList<>();
}
