package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@Entity
@Table(name = "acerto")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acerto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revendedor_id", nullable = false, updatable = false)
    private Revendedor revendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false, updatable = false)
    private LoteConsignacao lote;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_maleta_id", updatable = false)
    private DocumentoMaleta documentoMaleta;

    @Column(name = "data_acerto", nullable = false, updatable = false)
    private LocalDateTime dataAcerto = LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId());

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "valor_vendido_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorVendidoBruto;

    @Column(name = "percentual_comissao_aplicado", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualComissaoAplicado;

    @Column(name = "valor_comissao", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorComissao;

    @Column(name = "valor_devido", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDevido;

    @Column(name = "qtd_pecas_vendidas", nullable = false)
    private Integer qtdPecasVendidas;


}
