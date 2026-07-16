package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoteRepository extends JpaRepository<LoteConsignacao, Long> {

    Optional<LoteConsignacao> findByRevendedorIdAndStatus(Long revendedor_id, StatusLote status);
}
