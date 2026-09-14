package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.StatusAcerto;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import br.com.menegati.brb_revendedoras.services.PainelAdminService.AcertoAdminView;
import br.com.menegati.brb_revendedoras.services.PainelAdminService.LancamentoView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cobre só os métodos puros de derivação (saldo acumulado e alocação FIFO).
 * São estáticos e sem Spring de propósito: a regra de "quanto de cada acerto já foi pago"
 * não pode depender de banco para ser verificada.
 */
public class PainelAdminServiceTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 10, 10, 0);

    private LancamentoFinanceiro lancamento(TipoLancamento tipo, String valor, int diasAposBase) {
        return LancamentoFinanceiro.builder()
                .tipo(tipo)
                .carteira(CarteiraLancamento.DINHEIRO)
                .valor(new BigDecimal(valor))
                .data(BASE.plusDays(diasAposBase))
                .descricao("desc")
                .build();
    }

    private Acerto acerto(long id, String valorDevido, int diasAposBase) {
        return Acerto.builder()
                .id(id)
                .valorDevido(new BigDecimal(valorDevido))
                .dataAcerto(BASE.plusDays(diasAposBase))
                .build();
    }

    // ---------- calcularSaldoAcumulado ----------

    @Test
    @DisplayName("saldoApos acumula na ordem recebida e o último é o saldo devedor")
    void saldoAcumulado() {
        List<LancamentoView> views = PainelAdminService.calcularSaldoAcumulado(List.of(
                lancamento(TipoLancamento.DEBITO_ACERTO, "800.00", 0),
                lancamento(TipoLancamento.CREDITO_PAGAMENTO, "-300.00", 1),
                lancamento(TipoLancamento.AJUSTE_DEBITO, "50.00", 2)
        ));

        assertEquals(new BigDecimal("800.00"), views.get(0).saldoApos());
        assertEquals(new BigDecimal("500.00"), views.get(1).saldoApos());
        assertEquals(new BigDecimal("550.00"), views.get(2).saldoApos());
    }

    @Test
    @DisplayName("lista vazia produz lista vazia")
    void saldoAcumuladoVazio() {
        assertTrue(PainelAdminService.calcularSaldoAcumulado(List.of()).isEmpty());
    }

    // ---------- alocarPagamentosFifo ----------

    @Test
    @DisplayName("sem crédito nenhum, todo acerto fica EM_ABERTO com valorPago zero")
    void semPagamento() {
        List<AcertoAdminView> views = PainelAdminService.alocarPagamentosFifo(
                List.of(acerto(1L, "800.00", 0)),
                List.of(lancamento(TipoLancamento.DEBITO_ACERTO, "800.00", 0))
        );

        assertEquals(1, views.size());
        assertEquals(StatusAcerto.EM_ABERTO, views.get(0).status());
        assertEquals(0, BigDecimal.ZERO.compareTo(views.get(0).valorPago()));
    }

    @Test
    @DisplayName("pagamento menor que o acerto marca PARCIAL com o valor pago")
    void pagamentoParcial() {
        List<AcertoAdminView> views = PainelAdminService.alocarPagamentosFifo(
                List.of(acerto(1L, "800.00", 0)),
                List.of(
                        lancamento(TipoLancamento.DEBITO_ACERTO, "800.00", 0),
                        lancamento(TipoLancamento.CREDITO_PAGAMENTO, "-300.00", 5)
                )
        );

        assertEquals(StatusAcerto.PARCIAL, views.get(0).status());
        assertEquals(new BigDecimal("300.00"), views.get(0).valorPago());
    }

    @Test
    @DisplayName("crédito cobre o mais antigo primeiro: 1º QUITADO, 2º EM_ABERTO; retorno do mais recente ao mais antigo")
    void fifoEntreDoisAcertos() {
        // Entrada propositalmente fora de ordem (desc), como vem do repositório
        List<AcertoAdminView> views = PainelAdminService.alocarPagamentosFifo(
                List.of(acerto(2L, "500.00", 30), acerto(1L, "800.00", 0)),
                List.of(
                        lancamento(TipoLancamento.DEBITO_ACERTO, "800.00", 0),
                        lancamento(TipoLancamento.DEBITO_ACERTO, "500.00", 30),
                        lancamento(TipoLancamento.CREDITO_PAGAMENTO, "-800.00", 31)
                )
        );

        assertEquals(2L, views.get(0).acerto().getId());
        assertEquals(StatusAcerto.EM_ABERTO, views.get(0).status());

        assertEquals(1L, views.get(1).acerto().getId());
        assertEquals(StatusAcerto.QUITADO, views.get(1).status());
        assertEquals(new BigDecimal("800.00"), views.get(1).valorPago());
    }

    @Test
    @DisplayName("sobra de crédito transborda para o próximo acerto")
    void transbordoParaProximo() {
        List<AcertoAdminView> views = PainelAdminService.alocarPagamentosFifo(
                List.of(acerto(2L, "500.00", 30), acerto(1L, "800.00", 0)),
                List.of(lancamento(TipoLancamento.CREDITO_PAGAMENTO, "-1000.00", 31))
        );

        assertEquals(StatusAcerto.PARCIAL, views.get(0).status());
        assertEquals(new BigDecimal("200.00"), views.get(0).valorPago());
        assertEquals(StatusAcerto.QUITADO, views.get(1).status());
    }

    @Test
    @DisplayName("AJUSTE_CREDITO conta como crédito; AJUSTE_DEBITO não é alocado a acerto nenhum")
    void ajustes() {
        List<AcertoAdminView> views = PainelAdminService.alocarPagamentosFifo(
                List.of(acerto(1L, "800.00", 0)),
                List.of(
                        lancamento(TipoLancamento.DEBITO_ACERTO, "800.00", 0),
                        lancamento(TipoLancamento.AJUSTE_DEBITO, "999.00", 1),
                        lancamento(TipoLancamento.AJUSTE_CREDITO, "-800.00", 2)
                )
        );

        assertEquals(StatusAcerto.QUITADO, views.get(0).status());
        assertEquals(new BigDecimal("800.00"), views.get(0).valorPago());
    }
}
