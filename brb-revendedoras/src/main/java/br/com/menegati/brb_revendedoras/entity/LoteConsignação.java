package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.StatusLote;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "lote_consignação")
public class LoteConsignação {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento; // Ex: 2000047 vindos do PDF

    @ManyToOne
    @JoinColumn(name = "revendedora_id", nullable = false)
    private Revendedor revendedora;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLote status = StatusLote.ABERTO;

    @Column(name = "valor_total_estimado", precision = 10, scale = 2)
    private BigDecimal valorTotalEstimado = BigDecimal.ZERO;

    @Column(name = "valor_total_acertado", precision = 10, scale = 2)
    private BigDecimal valorTotalAcertado = BigDecimal.ZERO;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemConsignacao> itens = new ArrayList<>();
}
