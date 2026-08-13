package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.dto.revendedora.PainelRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.entity.CargaConsignacao;
import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.mapper.PainelRevendedoraMapper;
import br.com.menegati.brb_revendedoras.repository.AcertoRepository;
import br.com.menegati.brb_revendedoras.repository.CargaConsignacaoRepository;
import br.com.menegati.brb_revendedoras.repository.DocumentoMaletaRepository;
import br.com.menegati.brb_revendedoras.repository.LoteRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RevendedoraServiceTest {

    @Mock private PainelRevendedoraMapper mapper;
    @Mock private UserRepository userRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private DocumentoMaletaRepository documentoMaletaRepository;
    @Mock private AcertoRepository acertoRepository;
    @Mock private CargaConsignacaoRepository cargaConsignacaoRepository;
    @Mock private ContaCorrenteService contaCorrenteService;

    @InjectMocks
    private RevendedoraService revendedoraService;

    @Test
    @DisplayName("Deve montar o painel com o histórico de cargas vindo do CargaConsignacaoRepository")
    void deveMontarPainelComHistoricoDeCargas() {
        Revendedor revendedora = Revendedor.builder().id(1L).cpf("51967545120").build();
        LoteConsignacao loteAtual = LoteConsignacao.builder().id(10L).status(StatusLote.ABERTO).build();
        CargaConsignacao carga = CargaConsignacao.builder().id(5L).revendedor(revendedora).lote(loteAtual).build();
        List<CargaConsignacao> cargas = List.of(carga);
        PainelRevendedoraResponseDTO.CargaDTO cargaDTO =
                new PainelRevendedoraResponseDTO.CargaDTO(10L, "2000047", "01/01/2026", new BigDecimal("171.50"), 5);

        when(userRepository.findByCpf("51967545120")).thenReturn(Optional.of(revendedora));
        when(loteRepository.findByRevendedorIdAndStatus(1L, StatusLote.ABERTO)).thenReturn(Optional.of(loteAtual));
        when(documentoMaletaRepository.findByRevendedorIdOrderByLoteIdDesc(1L)).thenReturn(List.of());
        when(acertoRepository.findByRevendedorIdOrderByDataAcertoDesc(1L)).thenReturn(List.of());
        when(cargaConsignacaoRepository.findByRevendedorIdOrderByDataAberturaDesc(1L)).thenReturn(cargas);
        when(mapper.toCargaDTOList(cargas)).thenReturn(List.of(cargaDTO));
        when(contaCorrenteService.getSaldoDevedor(1L)).thenReturn(BigDecimal.ZERO);

        PainelRevendedoraResponseDTO response = revendedoraService.getRevendedoraData("51967545120");

        verify(cargaConsignacaoRepository).findByRevendedorIdOrderByDataAberturaDesc(1L);
        verify(mapper).toCargaDTOList(cargas);
        assertEquals(List.of(cargaDTO), response.getHistoricoCargas());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existir")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(userRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> revendedoraService.getRevendedoraData("00000000000"));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o usuário não for revendedora")
    void deveLancarExcecaoQuandoUsuarioNaoForRevendedora() {
        Cliente cliente = Cliente.builder().id(2L).cpf("11122233344").build();
        when(userRepository.findByCpf("11122233344")).thenReturn(Optional.of(cliente));

        assertThrows(BusinessException.class, () -> revendedoraService.getRevendedoraData("11122233344"));
    }
}
