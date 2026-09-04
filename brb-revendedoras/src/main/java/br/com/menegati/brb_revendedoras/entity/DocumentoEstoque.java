package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "documento_estoque")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class DocumentoEstoque extends DocumentoBase{

    @Builder.Default
    @Column(name = "linhas_salvas", nullable = false)
    private Integer linhasSalvas = 0;

    @Builder.Default
    @Column(name = "linhas_ignoradas", nullable = false)
    private Integer linhasIgnoradas = 0 ;



}
