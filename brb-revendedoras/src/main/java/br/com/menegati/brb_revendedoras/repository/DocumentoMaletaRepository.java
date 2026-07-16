package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoMaletaRepository extends JpaRepository<DocumentoMaleta, Long> {

    boolean existsByNumeroConsignacao(String numeroConsignacao);
}
