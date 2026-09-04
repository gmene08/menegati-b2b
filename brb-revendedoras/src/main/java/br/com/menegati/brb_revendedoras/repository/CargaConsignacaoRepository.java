package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.CargaConsignacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargaConsignacaoRepository extends JpaRepository<CargaConsignacao, Long> {
    List<CargaConsignacao> findByRevendedorIdOrderByDataAberturaDesc(Long revendedorId);

    List<CargaConsignacao> findAllByOrderByDataAberturaDesc(Pageable pageable);
}
