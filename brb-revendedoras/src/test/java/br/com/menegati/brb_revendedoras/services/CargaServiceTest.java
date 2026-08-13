package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.CargaConsignacao;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.entity.ItemCarga;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.entity.Produto;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.repository.CargaConsignacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CargaServiceTest {

    @Mock private CargaConsignacaoRepository cargaConsignacaoRepository;

    @InjectMocks
    private CargaService cargaService;

    private ItemCarga criarItem(int quantidade, String valorUnitario) {
        Produto produto = new Produto();
        produto.setCodigo("101094");
        return ItemCarga.builder()
                .produto(produto)
                .quantidade(quantidade)
                .valorUnitarioCongelado(new BigDecimal(valorUnitario))
                .documentoEntrada("2000047")
                .build();
    }

    @Test
    @DisplayName("Deve criar a carga somando quantidade e valor total dos itens e vinculando ao documento")
    void deveCriarCargaComTotaisCalculados() {
        DocumentoMaleta documento = DocumentoMaleta.builder()
                .tipoDocumento(TipoDocumento.MALETA_ENTRADA)
                .revendedor(Revendedor.builder().id(1L).build())
                .lote(LoteConsignacao.builder().id(10L).build())
                .build();

        List<ItemCarga> itens = List.of(criarItem(3, "50.50"), criarItem(2, "10.00"));

        when(cargaConsignacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CargaConsignacao carga = cargaService.criarCarga(documento, itens);

        assertEquals(new BigDecimal("171.50"), carga.getValorTotalEstimado(), "3x50,50 + 2x10,00 deve ser 171,50");
        assertEquals(5, carga.getQuantidadePecas(), "Soma das quantidades dos dois itens");
        assertEquals(documento, carga.getDocumentoMaleta());
        assertEquals(documento.getRevendedor(), carga.getRevendedor());
        assertEquals(documento.getLote(), carga.getLote());
        itens.forEach(item -> assertEquals(carga, item.getCarga(), "Cada item deve ficar vinculado à carga criada"));

        verify(cargaConsignacaoRepository).save(carga);
    }

    @Test
    @DisplayName("Não deve criar carga a partir de um documento que não seja MALETA_ENTRADA")
    void deveRejeitarDocumentoQueNaoSejaEntrada() {
        DocumentoMaleta documento = DocumentoMaleta.builder().tipoDocumento(TipoDocumento.MALETA_ACERTO).build();
        List<ItemCarga> itens = List.of(criarItem(1, "10.00"));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cargaService.criarCarga(documento, itens));

        assertEquals("Para criar carga a partir de um documento de maleta, o tipo de documento deve ser MALETA_ENTRADA",
                exception.getMessage());
        verify(cargaConsignacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar carga sem itens")
    void deveRejeitarCargaSemItens() {
        DocumentoMaleta documento = DocumentoMaleta.builder().tipoDocumento(TipoDocumento.MALETA_ENTRADA).build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cargaService.criarCarga(documento, List.of()));

        assertEquals("Não é possível criar uma carga sem itens.", exception.getMessage());
        verify(cargaConsignacaoRepository, never()).save(any());
    }
}
