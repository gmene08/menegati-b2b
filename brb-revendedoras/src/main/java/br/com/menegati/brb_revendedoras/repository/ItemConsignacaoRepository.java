package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.ItemConsignacao;
import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemConsignacaoRepository extends JpaRepository<ItemConsignacao, Long> {
    boolean existsByProdutoCodigo(String codigo);

    Optional<ItemConsignacao> findByProdutoCodigoAndLoteId(String produtoCodigo, Long loteId);

    Optional<ItemConsignacao> findFirstByProdutoCodigoAndLoteIdAndStatusItem(String codigo, Long id, StatusItemLote statusItemLote);
}
