package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import br.com.menegati.brb_revendedoras.repository.LancamentoFinanceiroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LancamentoFinanceiroServiceTest {

    @Mock private LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    @InjectMocks
    private LancamentoFinanceiroService lancamentoFinanceiroService;

    private Acerto criarAcerto(String numeroConsignacao) {
        Revendedor revendedor = Revendedor.builder().id(1L).name("Fulana").build();

        DocumentoMaleta documentoMaleta = numeroConsignacao == null ? null : DocumentoMaleta.builder()
                .tipoDocumento(TipoDocumento.MALETA_ACERTO)
                .numeroConsignacao(numeroConsignacao)
                .quantidadePecas(3)
                .valorTotal(new BigDecimal("100.00"))
                .revendedor(revendedor)
                .build();

        return Acerto.builder()
                .id(5L)
                .revendedor(revendedor)
                .documentoMaleta(documentoMaleta)
                .dataAcerto(LocalDateTime.of(2026, 1, 10, 9, 0))
                .dataVencimento(LocalDate.of(2026, 2, 9))
                .valorVendidoBruto(new BigDecimal("100.00"))
                .percentualComissaoAplicado(new BigDecimal("40.00"))
                .valorComissao(new BigDecimal("40.00"))
                .valorDevido(new BigDecimal("60.00"))
                .qtdPecasVendidas(3)
                .build();
    }

    @Test
    @DisplayName("Deve lançar o débito do acerto na carteira DINHEIRO (dívida em reais, não crédito de bônus)")
    void deveLancarDebitoNaCarteiraDinheiro() {
        Acerto acerto = criarAcerto("2000047");

        lancamentoFinanceiroService.criarLancamento(acerto);

        ArgumentCaptor<LancamentoFinanceiro> captor = ArgumentCaptor.forClass(LancamentoFinanceiro.class);
        verify(lancamentoFinanceiroRepository).save(captor.capture());
        LancamentoFinanceiro lancamento = captor.getValue();

        assertEquals(CarteiraLancamento.DINHEIRO, lancamento.getCarteira(), "Débito de acerto é dívida em reais, deve ficar na carteira DINHEIRO");
        assertEquals(TipoLancamento.DEBITO_ACERTO, lancamento.getTipo());
        assertEquals(acerto.getRevendedor(), lancamento.getRevendedor());
        assertEquals(acerto.getDataAcerto(), lancamento.getData());
        assertEquals(new BigDecimal("60.00"), lancamento.getValor());
        assertEquals(acerto, lancamento.getAcerto());
    }

    @Test
    @DisplayName("Deve preencher a descrição do lançamento com os dados do acerto e do documento, não deixar em branco")
    void deveGerarDescricaoComDadosDoAcertoEDocumento() {
        Acerto acerto = criarAcerto("2000047");

        lancamentoFinanceiroService.criarLancamento(acerto);

        ArgumentCaptor<LancamentoFinanceiro> captor = ArgumentCaptor.forClass(LancamentoFinanceiro.class);
        verify(lancamentoFinanceiroRepository).save(captor.capture());
        String descricao = captor.getValue().getDescricao();

        assertNotNull(descricao);
        assertFalse(descricao.isBlank(), "Descrição não deve ficar em branco - precisa ser rastreável até o acerto");
        assertTrue(descricao.contains("Fulana"), "Descrição deve citar o nome do revendedor");
        assertTrue(descricao.contains("2000047"), "Descrição deve citar o número da consignação do documento de acerto");
    }

    @Test
    @DisplayName("Deve indicar acerto manual na descrição quando não houver documento de maleta associado")
    void deveIndicarAcertoManualQuandoSemDocumento() {
        Acerto acerto = criarAcerto(null);

        lancamentoFinanceiroService.criarLancamento(acerto);

        ArgumentCaptor<LancamentoFinanceiro> captor = ArgumentCaptor.forClass(LancamentoFinanceiro.class);
        verify(lancamentoFinanceiroRepository).save(captor.capture());
        String descricao = captor.getValue().getDescricao();

        assertTrue(descricao.contains("manual"), "Descrição deve indicar que o acerto foi feito sem documento");
    }
}
