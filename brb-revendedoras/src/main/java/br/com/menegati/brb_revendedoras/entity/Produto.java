package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "produto")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @Column(length = 50) // Armazena o código SKU (Ex: 104994) do sistema legado
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    //Controla o que está fisicamente na loja, pronto para ir para uma nova maleta
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel = 0;

    @Column(name = "barras", nullable = true)
    private String barras;

    @Column(name = "cest", nullable = true)
    private String cest;

    @Column(name = "unidade", nullable = true)
    private String unidade;

    @Column(name = "ncm", nullable = true)
    private String ncm;


    private boolean ativo = true;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao = LocalDateTime.now();
}
