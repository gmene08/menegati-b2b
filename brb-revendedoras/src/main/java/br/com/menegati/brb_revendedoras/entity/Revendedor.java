package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "revendedora")
public class Revendedor extends User {

    @Column(name = "meta_mensal", precision = 10, scale = 2)
    private BigDecimal metaMensal = BigDecimal.ZERO;

    @Column(name = "valor_devido_atual", precision = 10, scale = 2)
    private BigDecimal valorDevidoAtual = BigDecimal.ZERO;
}
