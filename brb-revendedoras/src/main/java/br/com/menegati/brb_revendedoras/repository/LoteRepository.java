package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<LoteConsignacao, Long> {

    Optional<LoteConsignacao> findByRevendedorId(Long revendedor_id);

    Optional<LoteConsignacao> findByRevendedorIdAndStatus(Long revendedor_id, StatusLote status);

    @Query("SELECT l.revendedor.id, SUM(l.valorTotalEstimado) FROM LoteConsignacao l WHERE l.status = StatusLote.ABERTO GROUP BY l.revendedor.id")
    List<Object[]> getValorTotalEstimadoPorRevendedor();


}
