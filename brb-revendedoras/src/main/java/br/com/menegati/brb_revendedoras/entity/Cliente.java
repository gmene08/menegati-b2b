package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cliente")
@NoArgsConstructor
@SuperBuilder
public class Cliente extends User{

    public static abstract class ClienteBuilder<C extends Cliente, B extends ClienteBuilder<C, B>> extends UserBuilder<C, B> {
        public ClienteBuilder() {
            this.role(Role.CLIENTE);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.setRole(Role.CLIENTE);
    }
}
