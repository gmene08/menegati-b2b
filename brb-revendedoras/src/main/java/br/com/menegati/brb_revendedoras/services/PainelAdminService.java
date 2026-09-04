package br.com.menegati.brb_revendedoras.services;


import br.com.menegati.brb_revendedoras.dto.admin.PainelAdminResponseDTO;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import br.com.menegati.brb_revendedoras.mapper.PainelAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PainelAdminService {

    public record AdminRevendedoraView(
            Revendedor revendedor,
            BigDecimal saldoDevedor,
            BigDecimal saldoBonus,
            BigDecimal exposicaoMaleta,
            int diasAtraso,
            LocalDateTime dataUltimoAcerto,
            StatusLote loteStatus
    ) {}

    public record ProcessamentoView(Long id, TipoProcessamento tipo, OrigemProcessamento origem,
                                    String revendedora, LocalDateTime dataProcessamento,
                                    String numeroDocumento, int quantidadeItens,
                                    BigDecimal valorTotal) {
    }

    private final LoteService loteService;
    private final ContaCorrenteService contaCorrenteService;
    private final AcertoService acertoService;
    private final RevendedoraService revendedoraService;
    private final EstoqueService estoqueService;
    private final ProcessamentoService processamentoService;

    private final PainelAdminMapper mapper;

    public PainelAdminResponseDTO getPainelAdminData(){

        PainelAdminResponseDTO responseDTO = new PainelAdminResponseDTO();

        responseDTO.setRevendedoras(mapper.toAdminRevendedoraDTOList(getAdminRevendedoraViews()));
        responseDTO.setEstoque(mapper.toProdutoDTOList(estoqueService.getProdutos()));
        responseDTO.setProcessamentos(mapper.toProcessamentoDTOList(processamentoService.getUltimosProcessamentos()));
        return responseDTO;
    }

    private List<AdminRevendedoraView> getAdminRevendedoraViews(){
        List<Revendedor> revendedoras = revendedoraService.getAllRevendedoras();

        Map<Long, BigDecimal> saldoDevedorPorRevendedora = contaCorrenteService.getSaldoDevedores();
        Map<Long, BigDecimal> exposicaoMaletaPorRevendedora = loteService.getValorTotalEstimadoPorRevendedora();
        Map<Long, LocalDateTime> dataUltimoAcertoPorRevendedora = acertoService.getUltimasDatasDeAcerto();

        return revendedoras.stream().map(
                r -> new AdminRevendedoraView(
                        r,
                        saldoDevedorPorRevendedora.getOrDefault(r.getId(), BigDecimal.ZERO),
                        BigDecimal.ZERO, //TODO: implement this later when bonus is added
                        exposicaoMaletaPorRevendedora.getOrDefault(r.getId(), BigDecimal.ZERO),
                        0, //TODO: implement this later when expiration date is added
                        dataUltimoAcertoPorRevendedora.get(r.getId()),
                        exposicaoMaletaPorRevendedora.containsKey(r.getId()) ? StatusLote.ABERTO : StatusLote.FECHADO)

        ).toList();
    }

}
