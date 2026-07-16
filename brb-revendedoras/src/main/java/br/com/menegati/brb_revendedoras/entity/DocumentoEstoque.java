package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "documento_estoque")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class DocumentoEstoque extends DocumentoBase{

    @Column(name = "linhas_salvas")
    private Integer linhasSalvas;

    @Column(name = "linhas_ignoradas")
    private Integer linhasIgnoradas;

}
