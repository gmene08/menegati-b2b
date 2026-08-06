package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcertoRepository extends JpaRepository<Acerto, Long> {
    List<Acerto> findByRevendedorIdOrderByDataAcertoDesc(Long revendedorId);
}
