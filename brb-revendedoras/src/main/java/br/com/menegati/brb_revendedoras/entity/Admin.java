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
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "admin")
@NoArgsConstructor
@SuperBuilder
public class Admin extends User{

    public static abstract class AdminBuilder<C extends Admin, B extends AdminBuilder<C, B>> extends UserBuilder<C, B> {
        public AdminBuilder() {
            this.role(Role.ADMIN);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.setRole(Role.ADMIN);
    }
}
