package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
