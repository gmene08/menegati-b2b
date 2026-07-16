package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "documento_maleta")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class DocumentoMaleta extends DocumentoBase{

    @Column(name = "numero_consignacao", nullable = false, length = 50)
    private String numeroConsignacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revendedor_id", nullable = false)
    private Revendedor revendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteConsignacao lote;
}
