package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "documento_maleta")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class DocumentoMaleta extends DocumentoBase{

    @Column(name = "numero_consignacao", nullable = false, length = 50)
    private String numeroConsignacao;

    @Column(name = "quantidade_pecas", nullable = false)
    private Integer quantidadePecas;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revendedor_id", nullable = false)
    private Revendedor revendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteConsignacao lote;

}
