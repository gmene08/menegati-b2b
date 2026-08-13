package br.com.menegati.brb_revendedoras.entity;


import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
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
@Table(name = "item_carga")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "carga_id", nullable = false)
    private CargaConsignacao carga;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_codigo", nullable = false)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario_congelado", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitarioCongelado; // Evita problemas se o preço do produto mudar no catálogo global

    @Column(name = "documento_entrada", length = 50)
    private String documentoEntrada; // Guarda o número da Consignação que adicionou a peça

}
