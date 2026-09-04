package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.EntradaEstoque;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaEstoqueRepository extends JpaRepository<EntradaEstoque, Long> {

    List<EntradaEstoque> findAllByOrderByDataEntradaDesc(Pageable pageable);

}
