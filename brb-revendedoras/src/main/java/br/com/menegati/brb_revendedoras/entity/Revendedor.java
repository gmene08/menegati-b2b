package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "revendedora")
public class Revendedor extends User {

    public static abstract class RevendedorBuilder<C extends Revendedor, B extends RevendedorBuilder<C, B>> extends UserBuilder<C, B> {
        public RevendedorBuilder() {
            this.role(Role.REVENDEDOR);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.setRole(Role.REVENDEDOR);
    }

    @Column(name = "meta_mensal", precision = 10, scale = 2)
    private BigDecimal metaMensal = BigDecimal.ZERO;

    @Column(name = "valor_devido_atual", precision = 10, scale = 2)
    private BigDecimal valorDevidoAtual = BigDecimal.ZERO;
}
