package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Ao estender org.springframework.data.repository.Repository em vez de JpaRepository,
 * ocultamos fisicamente métodos como delete(), deleteById(), deleteAll() e saveAll().
 * Isso garante que o Ledger (Livro-Razão) seja APENAS de inserção (append-only).
 */

public interface LancamentoFinanceiroRepository extends Repository<LancamentoFinanceiro, Long> {

    // Expondo apenas a inserção e consultas específicas
    LancamentoFinanceiro save(LancamentoFinanceiro lancamentoFinanceiro);

    List<LancamentoFinanceiro> findByRevendedorIdOrderByDataAsc(Long revendedorId);

    List<LancamentoFinanceiro> findByRevendedorIdAndCarteiraOrderByDataAsc(Long revendedor_id, CarteiraLancamento carteira);

    @Query("SELECT l.revendedor.id, SUM(l.valor) FROM LancamentoFinanceiro l WHERE l.carteira=CarteiraLancamento.DINHEIRO GROUP BY l.revendedor.id")
    List<Object[]> somarSaldosDevedores();
}