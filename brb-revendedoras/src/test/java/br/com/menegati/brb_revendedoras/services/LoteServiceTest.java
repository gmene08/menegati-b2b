package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.*;
import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoteServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private ItemConsignacaoRepository itemConsignacaoRepository;
    @Mock private DocumentoMaletaRepository documentoMaletaRepository;

    @Spy
    @InjectMocks
    private LoteService loteService;

    @Test
    @DisplayName("Deve importar carga para o lote com sucesso")
    void deveImportarParaOLoteComSucesso(){
        String conteudoTextoExtraidoDoPdf = """
                CPF: 519.675.451-20
                Consignação: 2000047
                101094 ANEL FEMININO BRANCO TAM.17(UN) 3,000 50,50 50,50
                RODAPÉ BLA BLA BLA
                """;

        MockMultipartFile arquivoPdf = new MockMultipartFile(
                "file",
                "lote.pdf",
                "application/pdf",
                conteudoTextoExtraidoDoPdf.getBytes(StandardCharsets.ISO_8859_1) // Mesma codificação do seu Service
        );

        doReturn(conteudoTextoExtraidoDoPdf).when(loteService).extrairTextoDoPdf(arquivoPdf);

        Revendedor revendedor = Revendedor.builder().id(1L).build();

        Produto produto = new Produto();
        produto.setCodigo("101094");
        produto.setQuantidadeDisponivel(5);

        when(userRepository.findByCpf("51967545120")).thenReturn(Optional.of(revendedor));
        when(documentoMaletaRepository.existsByNumeroConsignacao("2000047")).thenReturn(false);
        when(loteRepository.findByRevendedorIdAndStatus(any(), any())).thenReturn(Optional.empty()); // nesse teste o caso é ser a primeira entrada de carga
        when(produtoRepository.findByCodigo("101094")).thenReturn(Optional.of(produto));

        when(loteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoteService.ProcessamentoLoteResult result = loteService.importarCargaMaletaPdf(arquivoPdf);

        assertNotNull(result, "O lote deve ser salvo com sucesso!");
        assertEquals(1, result.numeroDePecasDestePdf(), "Deve ter criado 1 item no lote");

        BigDecimal precoTotal = new BigDecimal("50.50").multiply(BigDecimal.valueOf(3L)); // coluna quantidade = 3
        assertEquals(precoTotal, result.valorTotalDestePdf(), "O valor total do lote deve ser igual a " + precoTotal);

        assertEquals(2, produto.getQuantidadeDisponivel(), "Deve ter atualizado a quantidade disponivel do produto");

        verify(documentoMaletaRepository, times(1)).save(any());

        LoteConsignacao loteConsignacao = result.lote();
        assertEquals(1, loteConsignacao.getItens().size(), "Deve ter criado 1 item no lote");
        assertEquals("2000047", loteConsignacao.getItens().getFirst().getDocumentoEntrada(), "O documento de entrada do item deve ser igual a 2000047");
        assertEquals(StatusItemLote.ENCARREGADO, loteConsignacao.getItens().getFirst().getStatusItem(), "O status do item deve ser ENCARREGADO");
        assertEquals(precoTotal, loteConsignacao.getValorTotalEstimado(), "O valor total do lote deve ser igual a " + precoTotal);
        assertEquals(new BigDecimal("0"), loteConsignacao.getValorTotalAcertado(), "O valor total do lote deve ser igual a 0,00"); // nesse teste o caso é ser a primeira entrada de carga
        assertEquals(revendedor, loteConsignacao.getRevendedor(), "O lote deve ser vinculado ao revendedor");
        assertEquals(StatusLote.ABERTO, loteConsignacao.getStatus(), "O lote deve estar em andamento");

    }

    // ==============================================================================
    // CENÁRIO 1: VENDA PARCIAL (Menor)
    // ==============================================================================
    @Test
    @DisplayName("Acerto: Deve realizar VENDA PARCIAL dividindo o item na maleta")
    void deveAcertarVendaParcial(){
        // A revendedora relata no PDF que vendeu 2 peças
        String pdf = "CPF: 519.675.451-20\nConsignação: 2000047\n101094 ANEL TAM.17(UN) 2,000 50,50 50,50\n";
        MockMultipartFile arquivoPdf = new MockMultipartFile("file", "pdf", "application/pdf", pdf.getBytes());
        doReturn(pdf).when(loteService).extrairTextoDoPdf(arquivoPdf);

        Revendedor revendedor = Revendedor.builder().id(1L).build();
        LoteConsignacao lote = LoteConsignacao.builder().id(1L).revendedor(revendedor).itens(new ArrayList<>())
                .valorTotalEstimado(new BigDecimal("151.50")).valorTotalAcertado(new BigDecimal("0.00")).build();

        // No banco de dados, ela tinha pegado 3 peças
        ItemConsignacao itemNoBanco = new ItemConsignacao();
        itemNoBanco.setStatusItem(StatusItemLote.ENCARREGADO);
        itemNoBanco.setQuantidade(3);
        itemNoBanco.setValorUnitarioCongelado(new BigDecimal("50.50"));

        when(userRepository.findByCpf("51967545120")).thenReturn(Optional.of(revendedor));
        when(loteRepository.findByRevendedorIdAndStatus(any(), any())).thenReturn(Optional.of(lote));
        when(itemConsignacaoRepository.findFirstByProdutoCodigoAndLoteIdAndStatusItem(any(), any(), any())).thenReturn(Optional.of(itemNoBanco));

        LoteService.ProcessamentoLoteResult result = loteService.acertarCargaMaletaPdf(arquivoPdf);

        // O valor acertado foi de 2 peças (R$ 101,00)
        assertEquals(new BigDecimal("101.00"), result.valorTotalDestePdf());

        // Verifica a Divisão do Item
        assertEquals(1, itemNoBanco.getQuantidade(), "O item original deve sobrar com 1 peça na maleta");
        assertEquals(StatusItemLote.ENCARREGADO, itemNoBanco.getStatusItem(), "O item original continua encarregado");

        ItemConsignacao itemVendido = lote.getItens().get(0);
        assertEquals(2, itemVendido.getQuantidade(), "O item espelho deve registrar 2 peças");
        assertEquals(StatusItemLote.ACERTADO_VENDIDO, itemVendido.getStatusItem(), "O item espelho deve estar vendido");
    }

    // ==============================================================================
    // CENÁRIO 2: VENDA TOTAL (Igual)
    // ==============================================================================
    @Test
    @DisplayName("Acerto: Deve realizar VENDA TOTAL alterando o status do item original")
    void deveAcertarVendaTotal(){
        // ARRANGE
        // A revendedora relata no PDF que vendeu 3 peças
        String pdf = "CPF: 519.675.451-20\nConsignação: 2000047\n101094 ANEL TAM.17(UN) 3,000 50,50 50,50\n";
        MockMultipartFile arquivoPdf = new MockMultipartFile("file", "pdf", "application/pdf", pdf.getBytes());
        doReturn(pdf).when(loteService).extrairTextoDoPdf(arquivoPdf);

        Revendedor revendedor = Revendedor.builder().id(1L).build();
        LoteConsignacao lote = LoteConsignacao.builder().id(1L).revendedor(revendedor).itens(new ArrayList<>())
                .valorTotalEstimado(new BigDecimal("151.50")).valorTotalAcertado(new BigDecimal("0.00")).build();

        // No banco de dados, ela tinha 3 peças
        ItemConsignacao itemNoBanco = new ItemConsignacao();
        itemNoBanco.setStatusItem(StatusItemLote.ENCARREGADO);
        itemNoBanco.setQuantidade(3);
        itemNoBanco.setValorUnitarioCongelado(new BigDecimal("50.50"));

        when(userRepository.findByCpf("51967545120")).thenReturn(Optional.of(revendedor));
        when(loteRepository.findByRevendedorIdAndStatus(any(), any())).thenReturn(Optional.of(lote));
        when(itemConsignacaoRepository.findFirstByProdutoCodigoAndLoteIdAndStatusItem(any(), any(), any())).thenReturn(Optional.of(itemNoBanco));

        LoteService.ProcessamentoLoteResult result = loteService.acertarCargaMaletaPdf(arquivoPdf);

        // O valor acertado foi total R$ 151,50
        assertEquals(new BigDecimal("151.50"), result.valorTotalDestePdf());

        // Verifica que NENHUM item novo foi adicionado na lista (aproveitou o mesmo)
        assertTrue(lote.getItens().isEmpty(), "Não deve criar item espelho na venda total");

        // Verifica a atualização do item
        assertEquals(3, itemNoBanco.getQuantidade(), "A quantidade se mantém 3");
        assertEquals(StatusItemLote.ACERTADO_VENDIDO, itemNoBanco.getStatusItem(), "O status mudou para vendido");
    }

    // ==============================================================================
    // CENÁRIO 3: ANOMALIA (Maior)
    // ==============================================================================
    @Test
    @DisplayName("Acerto: Deve tratar ANOMALIA quando PDF tentar acertar mais peças do que o estoque da maleta")
    void deveLimitarVendaAcimaDoEstoque(){

        // A revendedora relata no PDF QUE vendeu 5 peças
        String pdf = "CPF: 519.675.451-20\nConsignação: 2000047\n101094 ANEL TAM.17(UN) 5,000 50,50 50,50\n";
        MockMultipartFile arquivoPdf = new MockMultipartFile("file", "pdf", "application/pdf", pdf.getBytes());
        doReturn(pdf).when(loteService).extrairTextoDoPdf(arquivoPdf);

        Revendedor revendedor = Revendedor.builder().id(1L).build();
        LoteConsignacao lote = LoteConsignacao.builder().id(1L).revendedor(revendedor).itens(new ArrayList<>())
                .valorTotalEstimado(new BigDecimal("151.50")).valorTotalAcertado(new BigDecimal("0.00")).build();

        // No banco de dados, ela só tinha  3 peças
        ItemConsignacao itemNoBanco = new ItemConsignacao();
        itemNoBanco.setStatusItem(StatusItemLote.ENCARREGADO);
        itemNoBanco.setQuantidade(3);
        itemNoBanco.setValorUnitarioCongelado(new BigDecimal("50.50"));

        when(userRepository.findByCpf("51967545120")).thenReturn(Optional.of(revendedor));
        when(loteRepository.findByRevendedorIdAndStatus(any(), any())).thenReturn(Optional.of(lote));
        when(itemConsignacaoRepository.findFirstByProdutoCodigoAndLoteIdAndStatusItem(any(), any(), any())).thenReturn(Optional.of(itemNoBanco));

        LoteService.ProcessamentoLoteResult result = loteService.acertarCargaMaletaPdf(arquivoPdf);

        // O valor cobrado tem que travar no máximo que ela tinha (3 peças = R$ 151,50) e não cobrar as 5
        assertEquals(new BigDecimal("151.50"), result.valorTotalDestePdf(), "O sistema deve limitar a cobrança!");

        // Verifica a atualização segura do item
        assertEquals(3, itemNoBanco.getQuantidade(), "A quantidade travou nas 3 do banco");
        assertEquals(StatusItemLote.ACERTADO_VENDIDO, itemNoBanco.getStatusItem(), "As 3 foram dadas como vendidas");

        // Verifica se o aviso foi gerado para a administradora
        assertTrue(result.produtosAlertas().containsKey(101094L));
        assertTrue(result.produtosAlertas().get(101094L).contains("ALERTA: Quantidade no PDF (" + 5 + ") maior que a carregada. Limitado ao estoque da maleta (" + 3 + ")."));
    }

    @Test
    @DisplayName("Deve extrair o CPF limpo e a Consignação do texto bruto do PDF")
    void deveExtrairCpfEConsignacaoComSucesso() {

        String textoDoPdf = """
                MENEGATI JOIAS E SEMIJOIAS - Fone: (67)9967-3170
                Consignação: 2000047 Data: 05/06/2026 16:58
                Cliente.....: EUCINEIA ARTHEMAN DE MELO MENEGATTI - CPF: 519.675.451-20
                RUA PARANÁ, 552
                """;

        LoteService.DadosDoPdf dados = loteService.extrairDadosPrincipaisDoPdf(textoDoPdf);

        assertEquals("51967545120", dados.cpf(), "O CPF extraído deve vir sem os pontos e traços");
        assertEquals("2000047", dados.consignacao(), "O número da consignação deve ser exato");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se o PDF não contiver um CPF")
    void deveLancarExcecaoSeNaoAcharCpf(){
        String textoInvalido = "Consignação: 12345 Cliente: EUCINEIA ARTHEMAN Data: 05/06";

        BusinessException exception = assertThrows(BusinessException.class, ()->{
            loteService.extrairDadosPrincipaisDoPdf(textoInvalido);
        });

        assertEquals("Não foi possivel localizar o CPF da revendedora no PDF.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BusinessException se o PDF não contiver um numero de Consignacao")
    void deveLancarExcecaoSeNaoAcharConsignacao(){
        String textoInvalido = "CPF: 519.675.451-20 Cliente: EUCINEIA ARTHEMAN Data: 05/06";

        BusinessException exception = assertThrows(BusinessException.class, ()->{
            loteService.extrairDadosPrincipaisDoPdf(textoInvalido);
        });

        assertEquals("Nao foi possivel localizar o numero da consignacao no PDF.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar se o usuário NÃO for da role revendedor")
    void deveLancarExcecaoSeUsuarioNaoForRevendedor(){
        Cliente cliente = Cliente.builder().build();

        BusinessException exception = assertThrows(BusinessException.class, ()->{
            loteService.validarDadosPrincipaisDoPdf(cliente, null);
        });
        assertEquals("O perfil do cpf informado nao e revendedor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançcar exceção ao validar se o documento com o mesmo numero de consignação ja foi usado")
    void deveLancarExcecaoSeDocumentoJaForUsado(){
        String consignacao = "123456789";
        Revendedor revendedor = Revendedor.builder().build();

        when(documentoMaletaRepository.existsByNumeroConsignacao(consignacao)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, ()->{
            loteService.validarDadosPrincipaisDoPdf(revendedor, consignacao);
        });

        assertEquals("Documento com esse numero de consignação já foi processado uma vez.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ler a linha do produto corretamente")
    void deveLerLinhaDoProdutoCorretamente(){
        String texto = """
                CABEÇALHO BLA BLA BLA
                101094 ANEL FEMININO BRANCO COM TRES FILEIRAS DE CRYSTAIS TAM.17(UN) 1,000 50,50 50,50
                RODAPÉ BLA BLA BLA
                """;
        Matcher itemMatcher = Pattern.compile(LoteService.REGEX_LINHAS_PRODUTOS, Pattern.MULTILINE).matcher(texto);

        assertTrue(itemMatcher.find(), "A Regex deveria ter encontrado uma linha de produto válida!");

        String codigo = itemMatcher.group(1).trim();
        assertEquals("101094", codigo);

        String descricao = itemMatcher.group(2).trim();
        assertEquals("ANEL FEMININO BRANCO COM TRES FILEIRAS DE CRYSTAIS TAM.17(UN)", descricao);

        String quantidade = itemMatcher.group(3).trim();
        assertEquals("1,000", quantidade);

        String preco = itemMatcher.group(4).trim();
        assertEquals("50,50", preco);
    }


}
