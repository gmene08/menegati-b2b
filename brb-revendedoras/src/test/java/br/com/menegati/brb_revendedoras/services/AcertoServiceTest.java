package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.repository.AcertoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AcertoServiceTest {

    @Mock private AcertoRepository acertoRepository;
    @Mock private LancamentoFinanceiroService lancamentoFinanceiroService;

    @InjectMocks
    private AcertoService acertoService;

    private DocumentoMaleta criarDocumentoMaletaAcerto(BigDecimal valorTotal, LocalDateTime dataProcessamento) {
        Revendedor revendedor = Revendedor.builder().id(1L).name("Fulana").percentualComissao(new BigDecimal("40.00")).build();
        LoteConsignacao lote = LoteConsignacao.builder().id(1L).revendedor(revendedor).build();

        return DocumentoMaleta.builder()
                .id(10L)
                .tipoDocumento(TipoDocumento.MALETA_ACERTO)
                .numeroConsignacao("2000047")
                .quantidadePecas(3)
                .valorTotal(valorTotal)
                .dataProcessamento(dataProcessamento)
                .revendedor(revendedor)
                .lote(lote)
                .build();
    }

    @Test
    @DisplayName("Deve criar acerto com 40% de comissão e 60% de dívida sobre o valor vendido")
    void deveCriarAcertoComComissaoEComDivida() {
        DocumentoMaleta documentoMaleta = criarDocumentoMaletaAcerto(new BigDecimal("100.00"), LocalDateTime.of(2026, 1, 10, 9, 0));

        when(acertoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        acertoService.criarAcerto(documentoMaleta);

        ArgumentCaptor<Acerto> captor = ArgumentCaptor.forClass(Acerto.class);
        verify(acertoRepository).save(captor.capture());
        Acerto acertoSalvo = captor.getValue();

        assertEquals(new BigDecimal("100.00"), acertoSalvo.getValorVendidoBruto());
        assertEquals(new BigDecimal("40.00"), acertoSalvo.getValorComissao(), "Comissão deve ser 40% do valor vendido");
        assertEquals(new BigDecimal("60.00"), acertoSalvo.getValorDevido(), "Dívida deve ser 60% do valor vendido");
        assertEquals(new BigDecimal("40.00"), acertoSalvo.getPercentualComissaoAplicado());
        assertEquals(3, acertoSalvo.getQtdPecasVendidas());
        assertEquals(documentoMaleta, acertoSalvo.getDocumentoMaleta());
        assertEquals(documentoMaleta.getRevendedor(), acertoSalvo.getRevendedor());
        assertEquals(documentoMaleta.getLote(), acertoSalvo.getLote());
    }

    @Test
    @DisplayName("Data de vencimento deve ser 30 dias após a data de processamento do documento, não a data atual do sistema")
    void deveCalcularVencimentoAPartirDaDataDoDocumento() {
        LocalDateTime dataProcessamentoNoPassado = LocalDateTime.of(2020, 3, 1, 8, 30);
        DocumentoMaleta documentoMaleta = criarDocumentoMaletaAcerto(new BigDecimal("100.00"), dataProcessamentoNoPassado);

        when(acertoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        acertoService.criarAcerto(documentoMaleta);

        ArgumentCaptor<Acerto> captor = ArgumentCaptor.forClass(Acerto.class);
        verify(acertoRepository).save(captor.capture());

        assertEquals(dataProcessamentoNoPassado.toLocalDate().plusDays(30), captor.getValue().getDataVencimento(),
                "Vencimento deve ser derivado da data do documento, não de LocalDate.now()");
    }

    @Test
    @DisplayName("Deve gerar o lançamento financeiro a partir do acerto recém-criado")
    void deveGerarLancamentoFinanceiroParaOAcerto() {
        DocumentoMaleta documentoMaleta = criarDocumentoMaletaAcerto(new BigDecimal("100.00"), LocalDateTime.of(2026, 1, 10, 9, 0));

        when(acertoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        acertoService.criarAcerto(documentoMaleta);

        ArgumentCaptor<Acerto> captor = ArgumentCaptor.forClass(Acerto.class);
        verify(lancamentoFinanceiroService).criarLancamento(captor.capture());
        assertEquals(new BigDecimal("60.00"), captor.getValue().getValorDevido());
    }

    @Test
    @DisplayName("Deve rejeitar documento que não seja do tipo MALETA_ACERTO")
    void deveRejeitarDocumentoQueNaoSejaDeAcerto() {
        DocumentoMaleta documentoDeCarga = criarDocumentoMaletaAcerto(new BigDecimal("100.00"), LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()));
        documentoDeCarga.setTipoDocumento(TipoDocumento.MALETA_ENTRADA);

        assertThrows(BusinessException.class, () -> acertoService.criarAcerto(documentoDeCarga));
        verifyNoInteractions(acertoRepository, lancamentoFinanceiroService);
    }
}
