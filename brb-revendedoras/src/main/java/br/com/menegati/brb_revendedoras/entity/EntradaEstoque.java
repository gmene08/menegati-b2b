package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Data
@Entity
@Table(name = "entrada_estoque")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntradaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_entrada", nullable = false, updatable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId());

    @Column(length = 500, updatable = false)
    private String observacao;

    @Column(name = "registrado_por", length = 100, updatable = false)
    private String registradoPor;

    @OneToMany(mappedBy = "entrada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntradaEstoque> itens = new ArrayList<>();
}
