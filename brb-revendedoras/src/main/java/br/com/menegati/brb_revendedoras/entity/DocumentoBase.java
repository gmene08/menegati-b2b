package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public class DocumentoBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nome_arquivo",nullable = false)
    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_documento",nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name="data_processamento",nullable = false, updatable = false)
    private LocalDateTime dataProcessamento = LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId());

}
