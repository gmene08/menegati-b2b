package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "item_entrada_estoque")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEntradaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", nullable = false)
    private EntradaEstoque entrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_codigo", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;
}
