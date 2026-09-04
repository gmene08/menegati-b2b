package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Admin;
import br.com.menegati.brb_revendedoras.entity.EntradaEstoque;
import br.com.menegati.brb_revendedoras.entity.ItemEntradaEstoque;
import br.com.menegati.brb_revendedoras.entity.Produto;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.repository.EntradaEstoqueRepository;
import br.com.menegati.brb_revendedoras.repository.ProdutoRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceTest {

    private static final String CPF_ADMIN = "12345678900";

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntradaEstoqueRepository entradaEstoqueRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    @Captor
    private ArgumentCaptor<List<Produto>> produtosCaptor;

    @Captor
    private ArgumentCaptor<EntradaEstoque> entradaEstoqueCaptor;

    private void mockAdminValido() {
        Admin adminFake = Admin.builder()
                .name("Admin Teste")
                .cpf(CPF_ADMIN)
                .build();

        when(userRepository.findByCpf(CPF_ADMIN)).thenReturn(Optional.of(adminFake));
    }

    @Test
    @DisplayName("Deve processar um arquivo CSV perfeito e salvar todos os produtos")
    void deveProcessarUmArquivoCSVPerfeito(){
        mockAdminValido();
        String conteudoCsv = """
                Codigo;Nome;Venda;NCM;CEST;Unidade;Barras
                100001;ANEL OPALINA;146,00;71131100;12345;UN;78910
                100002;BRINCO ÁGATA;50,50;71131100;12345;UN;78911
                """;

        MockMultipartFile arquivoFalso = new MockMultipartFile(
                "file",
                "estoque.csv",
                "text/csv",
                conteudoCsv.getBytes(StandardCharsets.ISO_8859_1) // Mesma codificação do seu Service
        );

        when(produtoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());

        List<Long> linhasIgnoradas = estoqueService.registrarEstoque(arquivoFalso, CPF_ADMIN);

        assertNull(linhasIgnoradas, "Deve retornar null se tudo der certo");

        verify(produtoRepository, times(1)).saveAll(produtosCaptor.capture());

        List<Produto> produtosSalvos = produtosCaptor.getValue();

        assertEquals(2, produtosSalvos.size(), "Deve ter sido salvo 2 produtos");

        assertEquals("100001", produtosSalvos.getFirst().getCodigo(), "Codigo do produto salvo deve ser igual ao informado no CSV");
        assertEquals("ANEL OPALINA", produtosSalvos.getFirst().getNome(), "Nome do produto salvo deve ser igual ao informado no CSV");
        assertEquals(new BigDecimal("146.00"), produtosSalvos.getFirst().getPrecoVenda(), "Predo deve ser igual ao informado no CSV");
        assertEquals(1, produtosSalvos.getFirst().getQuantidadeDisponivel(), "O estoque inicial deve ser 1");

        verify(entradaEstoqueRepository, times(1)).save(entradaEstoqueCaptor.capture());
        EntradaEstoque entradaSalva = entradaEstoqueCaptor.getValue();

        assertEquals("Admin Teste", entradaSalva.getRegistradoPor(), "Deve registrar o nome do admin autenticado");
        assertNotNull(entradaSalva.getObservacao(), "Deve preencher a observação da entrada");

        List<ItemEntradaEstoque> itens = entradaSalva.getItens();
        assertEquals(2, itens.size(), "Deve ter um item de entrada por produto salvo");
        assertTrue(itens.stream().allMatch(item -> item.getEntrada() == entradaSalva), "Cada item deve apontar de volta para a entrada");
        assertTrue(itens.stream().anyMatch(item -> "100001".equals(item.getProduto().getCodigo()) && item.getQuantidade() == 1));
        assertTrue(itens.stream().anyMatch(item -> "100002".equals(item.getProduto().getCodigo()) && item.getQuantidade() == 1));
    }

    @Test
    @DisplayName("Deve ignorar linhas com dados faltando ou incorretos")
    public void deveIgnorarLinhasComDadosFaltandoOuIncorretos(){
        mockAdminValido();

        // A Linha 2 tem um preço em texto (inválido)
        // A Linha 3 não tem Código
        // A Linha 4 é perfeitamente válida
        String conteudoCsv = """
                Codigo;Nome;Venda;NCM;CEST;Unidade;Barras
                100003;ANEL DE OURO;PRECO_ERRADO;7113;123;UN;789
                ;BRINCO SEM CODIGO;50,00;7113;123;UN;789
                100005;PULSEIRA VALIDA;100,00;7113;123;UN;789
                """;

        MockMultipartFile arquivoFalso = new MockMultipartFile(
                "file", "estoque.csv", "text/csv", conteudoCsv.getBytes(StandardCharsets.ISO_8859_1)
        );

        when(produtoRepository.findByCodigo("100005")).thenReturn(Optional.empty());

        List<Long> linhasIgnoradas = estoqueService.registrarEstoque(arquivoFalso, CPF_ADMIN);

        assertNotNull(linhasIgnoradas, "Deve retornar uma lista de linhas ignoradas");
        assertEquals(2, linhasIgnoradas.size(), "Deve ter ignorado 2 linhas");

        assertTrue(linhasIgnoradas.contains(1L), "Deve ter ignorado a linha 2");
        assertTrue(linhasIgnoradas.contains(2L), "Deve ter ignorado a linha 3");

        verify(produtoRepository, times(1)).saveAll(produtosCaptor.capture());
        List<Produto> produtosSalvos = produtosCaptor.getValue();

        assertEquals(1, produtosSalvos.size(), "Deve ter salvo apenas 1 produto");
        assertEquals("100005", produtosSalvos.getFirst().getCodigo(), "Codigo do produto salvo deve ser igual ao informado no CSV");

        verify(entradaEstoqueRepository, times(1)).save(entradaEstoqueCaptor.capture());
        EntradaEstoque entradaSalva = entradaEstoqueCaptor.getValue();

        assertEquals(1, entradaSalva.getItens().size(), "Linhas ignoradas nao devem virar item de entrada");
        assertEquals("100005", entradaSalva.getItens().getFirst().getProduto().getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar a importação quando o CPF do registrador não pertence a um usuário cadastrado")
    void deveRejeitarQuandoCpfDoRegistradorForInvalido(){
        String conteudoCsv = """
                Codigo;Nome;Venda;NCM;CEST;Unidade;Barras
                100001;ANEL OPALINA;146,00;71131100;12345;UN;78910
                """;

        MockMultipartFile arquivoFalso = new MockMultipartFile(
                "file", "estoque.csv", "text/csv", conteudoCsv.getBytes(StandardCharsets.ISO_8859_1)
        );

        String cpfInvalido = "00000000000";
        when(userRepository.findByCpf(cpfInvalido)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> estoqueService.registrarEstoque(arquivoFalso, cpfInvalido),
                "Deve lançar ResourceNotFoundException quando o CPF não corresponde a um usuário");

        verifyNoInteractions(produtoRepository, entradaEstoqueRepository);
    }
}
