package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.entity.Pagamento;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.FormaPagamento;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.mapper.ExtratoMapper;
import br.com.menegati.brb_revendedoras.repository.LancamentoFinanceiroRepository;
import br.com.menegati.brb_revendedoras.repository.PagamentoRepository;
import br.com.menegati.brb_revendedoras.repository.RevendedoraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContaCorrenteServiceTest {

    @Mock private LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    @Mock private RevendedoraRepository revendedoraRepository;
    @Mock private LancamentoFinanceiroService lancamentoFinanceiroService;
    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private ExtratoMapper extratoMapper;

    @InjectMocks
    private ContaCorrenteService contaCorrenteService;

    private LancamentoFinanceiro criarLancamento(CarteiraLancamento carteira, BigDecimal valor) {
        return criarLancamento(carteira, TipoLancamento.DEBITO_ACERTO, valor);
    }

    private LancamentoFinanceiro criarLancamento(CarteiraLancamento carteira, TipoLancamento tipo, BigDecimal valor) {
        Revendedor revendedor = Revendedor.builder().id(1L).build();
        return LancamentoFinanceiro.builder()
                .revendedor(revendedor)
                .data(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()))
                .tipo(tipo)
                .carteira(carteira)
                .valor(valor)
                .descricao("desc")
                .build();
    }

    @Test
    @DisplayName("Saldo devedor deve somar apenas lançamentos da carteira DINHEIRO")
    void deveSomarApenasCarteiraDinheiro() {
        List<LancamentoFinanceiro> lancamentos = List.of(
                criarLancamento(CarteiraLancamento.DINHEIRO, new BigDecimal("60.00")),
                criarLancamento(CarteiraLancamento.DINHEIRO, new BigDecimal("30.00")),
                criarLancamento(CarteiraLancamento.CREDITO_PECAS, new BigDecimal("500.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(lancamentos);

        BigDecimal saldoDevedor = contaCorrenteService.getSaldoDevedor(1L);

        assertEquals(new BigDecimal("90.00"), saldoDevedor, "Crédito de bônus (CREDITO_PECAS) não pode entrar no saldo devedor em reais");
    }

    @Test
    @DisplayName("Saldo devedor deve ser zero quando só existem lançamentos de bônus")
    void deveRetornarZeroQuandoSoExisteBonus() {
        List<LancamentoFinanceiro> lancamentos = List.of(
                criarLancamento(CarteiraLancamento.CREDITO_PECAS, new BigDecimal("500.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(lancamentos);

        BigDecimal saldoDevedor = contaCorrenteService.getSaldoDevedor(1L);

        assertEquals(0, new BigDecimal("0.00").compareTo(saldoDevedor));
    }

    @Test
    @DisplayName("Saldo devedor deve ser zero quando não há lançamentos")
    void deveRetornarZeroSemLancamentos() {
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(List.of());

        BigDecimal saldoDevedor = contaCorrenteService.getSaldoDevedor(1L);

        assertEquals(0, BigDecimal.ZERO.compareTo(saldoDevedor));
    }

    @Test
    @DisplayName("Histórico financeiro deve retornar todos os lançamentos do revendedor, de todas as carteiras")
    void deveRetornarHistoricoCompleto() {
        List<LancamentoFinanceiro> lancamentos = List.of(
                criarLancamento(CarteiraLancamento.DINHEIRO, new BigDecimal("60.00")),
                criarLancamento(CarteiraLancamento.CREDITO_PECAS, new BigDecimal("500.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(lancamentos);

        List<LancamentoFinanceiro> historico = contaCorrenteService.getHistoricoFinanceiro(1L);

        assertEquals(2, historico.size());
    }

    @Test
    @DisplayName("Dívida de 600 quitada em duas parcelas (250 + 350) deve zerar o saldo devedor")
    void devePermitirPagamentoParcialAteQuitarDivida() {
        Revendedor revendedora = Revendedor.builder().id(1L).name("Fulana").build();
        when(revendedoraRepository.findById(1L)).thenReturn(Optional.of(revendedora));
        when(pagamentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<LancamentoFinanceiro> apenasDivida = List.of(
                criarLancamento(CarteiraLancamento.DINHEIRO, new BigDecimal("600.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(apenasDivida);

        contaCorrenteService.salvarPagamento(1L, 250f, FormaPagamento.PIX, null);

        ArgumentCaptor<Pagamento> primeiroPagamento = ArgumentCaptor.forClass(Pagamento.class);
        verify(lancamentoFinanceiroService).criarLancamento(primeiroPagamento.capture());
        assertEquals(0, new BigDecimal("250.00").compareTo(primeiroPagamento.getValue().getValor()),
                "Valor do pagamento não pode carregar erro de arredondamento do float");

        List<LancamentoFinanceiro> divida600MenosPago250 = List.of(
                criarLancamento(CarteiraLancamento.DINHEIRO, TipoLancamento.DEBITO_ACERTO, new BigDecimal("600.00")),
                criarLancamento(CarteiraLancamento.DINHEIRO, TipoLancamento.CREDITO_PAGAMENTO, new BigDecimal("250.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(divida600MenosPago250);

        assertEquals(0, new BigDecimal("350.00").compareTo(contaCorrenteService.getSaldoDevedor(1L)),
                "Após pagar 250 de uma dívida de 600, deve restar 350");

        contaCorrenteService.salvarPagamento(1L, 350f, FormaPagamento.PIX, null);
        contaCorrenteService.salvarPagamento(1L, 350f, FormaPagamento.PIX, null);
        List<LancamentoFinanceiro> dividaQuitada = List.of(
                criarLancamento(CarteiraLancamento.DINHEIRO, TipoLancamento.DEBITO_ACERTO, new BigDecimal("600.00")),
                criarLancamento(CarteiraLancamento.DINHEIRO, TipoLancamento.CREDITO_PAGAMENTO, new BigDecimal("250.00")),
                criarLancamento(CarteiraLancamento.DINHEIRO, TipoLancamento.CREDITO_PAGAMENTO, new BigDecimal("350.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(dividaQuitada);

        assertEquals(0, BigDecimal.ZERO.compareTo(contaCorrenteService.getSaldoDevedor(1L)),
                "Após pagar o total da dívida, o saldo devedor deve zerar");
    }

    @Test
    @DisplayName("Não deve permitir pagamento maior que o saldo devedor atual")
    void deveRejeitarPagamentoMaiorQueSaldoDevedor() {
        Revendedor revendedora = Revendedor.builder().id(1L).name("Fulana").build();
        when(revendedoraRepository.findById(1L)).thenReturn(Optional.of(revendedora));

        List<LancamentoFinanceiro> divida600 = List.of(
                criarLancamento(CarteiraLancamento.DINHEIRO, new BigDecimal("600.00"))
        );
        when(lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(1L)).thenReturn(divida600);

        assertThrows(BusinessException.class,
                () -> contaCorrenteService.salvarPagamento(1L, 700f, FormaPagamento.PIX, null));

        verify(pagamentoRepository, never()).save(any());
        verify(lancamentoFinanceiroService, never()).criarLancamento(any(Pagamento.class));
    }
}
