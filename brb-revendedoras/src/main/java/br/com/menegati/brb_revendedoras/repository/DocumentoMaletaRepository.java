package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoMaletaRepository extends JpaRepository<DocumentoMaleta, Long> {

    boolean existsByNumeroConsignacao(String numeroConsignacao);

    List<DocumentoMaleta> findByRevendedorIdOrderByLoteIdDesc(Long revendedorId);
}
