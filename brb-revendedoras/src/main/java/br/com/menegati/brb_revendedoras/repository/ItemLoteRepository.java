package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.ItemLote;
import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemLoteRepository extends JpaRepository<ItemLote, Long> {
    boolean existsByProdutoCodigo(String codigo);

    Optional<ItemLote> findByProdutoCodigoAndLoteId(String produtoCodigo, Long loteId);

    Optional<ItemLote> findFirstByProdutoCodigoAndLoteIdAndStatusItem(String codigo, Long id, StatusItemLote statusItemLote);
}
