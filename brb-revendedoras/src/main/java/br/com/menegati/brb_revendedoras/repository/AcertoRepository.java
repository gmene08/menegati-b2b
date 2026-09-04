package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcertoRepository extends JpaRepository<Acerto, Long> {
    List<Acerto> findByRevendedorIdOrderByDataAcertoDesc(Long revendedorId);

    @Query("SELECT a.revendedor.id, MAX(a.dataAcerto) FROM Acerto a GROUP BY a.revendedor.id")
    List<Object[]> getUltimaDataAcertoPorRevendedor();

    List<Acerto> findAllByOrderByDataAcertoDesc(Pageable pageable);
}
