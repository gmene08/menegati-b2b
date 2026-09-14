package br.com.menegati.brb_revendedoras.services;


import br.com.menegati.brb_revendedoras.dto.admin.DetalheRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.dto.admin.PainelAdminResponseDTO;
import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.StatusAcerto;
import br.com.menegati.brb_revendedoras.enums.StatusDividaRevendedora;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.mapper.DetalheRevendedoraMapper;
import br.com.menegati.brb_revendedoras.mapper.PainelAdminMapper;
import br.com.menegati.brb_revendedoras.repository.AcertoRepository;
import br.com.menegati.brb_revendedoras.repository.RevendedoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PainelAdminService {

    public record AdminRevendedoraView(
            Revendedor revendedor,
            BigDecimal saldoDevedor,
            BigDecimal saldoBonus,
            BigDecimal exposicaoMaleta,
            int diasAtraso,
            StatusDividaRevendedora status,
            LocalDateTime dataUltimoAcerto,
            StatusLote loteStatus
    ) {}

    public record ProcessamentoView(Long id, TipoProcessamento tipo, OrigemProcessamento origem,
                                    String revendedora, LocalDateTime dataProcessamento,
                                    String numeroDocumento, int quantidadeItens,
                                    BigDecimal valorTotal) {
    }

    public record LancamentoView(LancamentoFinanceiro lancamento, BigDecimal saldoApos) {}

    public record AcertoAdminView(Acerto acerto, BigDecimal valorPago, StatusAcerto status) {}

    private final LoteService loteService;
    private final ContaCorrenteService contaCorrenteService;
    private final AcertoService acertoService;
    private final RevendedoraService revendedoraService;
    private final EstoqueService estoqueService;
    private final ProcessamentoService processamentoService;
    private final RevendedoraRepository revendedoraRepository;
    private final AcertoRepository acertoRepository;

    private final PainelAdminMapper mapper;
    private final DetalheRevendedoraMapper detalheMapper;

    public PainelAdminResponseDTO getPainelAdminData(){

        PainelAdminResponseDTO responseDTO = new PainelAdminResponseDTO();

        responseDTO.setRevendedoras(mapper.toAdminRevendedoraDTOList(getAdminRevendedoraViews()));
        responseDTO.setEstoque(mapper.toProdutoDTOList(estoqueService.getProdutos()));
        responseDTO.setProcessamentos(mapper.toProcessamentoDTOList(processamentoService.getUltimosProcessamentos()));
        return responseDTO;
    }

    public DetalheRevendedoraResponseDTO getDetalheRevendedora(Long revendedorId){
        Revendedor revendedora = revendedoraRepository.findById(revendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Revendedora não encontrada"));

        // Só a carteira DINHEIRO: a coluna "Saldo" do extrato é o saldo devedor. Bônus terá extrato próprio.
        List<LancamentoFinanceiro> lancamentos = contaCorrenteService.getHistoricoFinanceiro(revendedorId, CarteiraLancamento.DINHEIRO);
        List<Acerto> acertos = acertoRepository.findByRevendedorIdOrderByDataAcertoDesc(revendedorId);

        List<LancamentoView> lancamentoViews = calcularSaldoAcumulado(lancamentos);
        BigDecimal saldoDevedor = lancamentoViews.isEmpty()
                ? BigDecimal.ZERO
                : lancamentoViews.getLast().saldoApos();

        Optional<LoteConsignacao> loteAberto = loteService.getLoteAberto(revendedorId);
        AdminRevendedoraView resumo = montarView(
                revendedora,
                saldoDevedor,
                loteAberto.map(LoteConsignacao::getValorTotalEstimado).orElse(BigDecimal.ZERO),
                acertos.isEmpty() ? null : acertos.getFirst().getDataAcerto(),
                loteAberto.isPresent() ? StatusLote.ABERTO : StatusLote.FECHADO);

        DetalheRevendedoraResponseDTO responseDTO = new DetalheRevendedoraResponseDTO();
        responseDTO.setResumo(mapper.toAdminRevendedoraDTO(resumo));
        responseDTO.setLancamentos(detalheMapper.toLancamentoDTOList(lancamentoViews));
        responseDTO.setAcertos(detalheMapper.toAcertoAdminDTOList(alocarPagamentosFifo(acertos, lancamentos)));
        return responseDTO;
    }

    private List<AdminRevendedoraView> getAdminRevendedoraViews(){
        List<Revendedor> revendedoras = revendedoraService.getAllRevendedoras();

        Map<Long, BigDecimal> saldoDevedorPorRevendedora = contaCorrenteService.getSaldoDevedores();
        Map<Long, BigDecimal> exposicaoMaletaPorRevendedora = loteService.getValorTotalEstimadoPorRevendedora();
        Map<Long, LocalDateTime> dataUltimoAcertoPorRevendedora = acertoService.getUltimasDatasDeAcerto();

        return revendedoras.stream().map(
                r -> montarView(
                        r,
                        saldoDevedorPorRevendedora.getOrDefault(r.getId(), BigDecimal.ZERO),
                        exposicaoMaletaPorRevendedora.getOrDefault(r.getId(), BigDecimal.ZERO),
                        dataUltimoAcertoPorRevendedora.get(r.getId()),
                        exposicaoMaletaPorRevendedora.containsKey(r.getId()) ? StatusLote.ABERTO : StatusLote.FECHADO)
        ).toList();
    }

    private AdminRevendedoraView montarView(Revendedor revendedor, BigDecimal saldoDevedor,
                                            BigDecimal exposicaoMaleta, LocalDateTime dataUltimoAcerto,
                                            StatusLote loteStatus){
        int diasAtraso = 0; //TODO: implement this later when expiration date is added
        return new AdminRevendedoraView(
                revendedor,
                saldoDevedor,
                BigDecimal.ZERO, //TODO: implement this later when bonus is added
                exposicaoMaleta,
                diasAtraso,
                StatusDividaRevendedora.de(saldoDevedor, diasAtraso),
                dataUltimoAcerto,
                loteStatus);
    }

    /**
     * Recebe os lançamentos em ordem cronológica e anexa a cada um o saldo após aplicá-lo.
     * O último saldoApos é, por construção, o saldo devedor atual.
     */
    static List<LancamentoView> calcularSaldoAcumulado(List<LancamentoFinanceiro> lancamentos){
        List<LancamentoView> views = new ArrayList<>(lancamentos.size());
        BigDecimal saldo = BigDecimal.ZERO;
        for (LancamentoFinanceiro l : lancamentos) {
            saldo = saldo.add(l.getValor());
            views.add(new LancamentoView(l, saldo));
        }
        return views;
    }

    /**
     * Deriva valorPago/status de cada acerto por alocação FIFO: todo crédito da carteira
     * (lançamentos com valor negativo — pagamento ou ajuste de crédito) abate o acerto mais antigo primeiro.
     * Nada é persistido; o razão continua sendo a única fonte da verdade.
     *
     * AJUSTE_DEBITO não é alocado a acerto nenhum: ele só aumenta o saldo devedor do resumo.
     * Por isso é possível todos os acertos estarem QUITADOS e ainda haver saldo em aberto.
     *
     * Retorna do mais recente para o mais antigo, como o painel lista.
     */
    static List<AcertoAdminView> alocarPagamentosFifo(List<Acerto> acertos, List<LancamentoFinanceiro> lancamentos){
        BigDecimal creditoRestante = lancamentos.stream()
                .map(LancamentoFinanceiro::getValor)
                .filter(v -> v.signum() < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .negate();

        List<Acerto> doMaisAntigo = acertos.stream()
                .sorted(Comparator.comparing(Acerto::getDataAcerto))
                .toList();

        List<AcertoAdminView> views = new ArrayList<>(acertos.size());
        for (Acerto acerto : doMaisAntigo) {
            BigDecimal pago = creditoRestante.min(acerto.getValorDevido());
            creditoRestante = creditoRestante.subtract(pago);

            StatusAcerto status;
            if (pago.signum() == 0) {
                status = StatusAcerto.EM_ABERTO;
            } else if (pago.compareTo(acerto.getValorDevido()) < 0) {
                status = StatusAcerto.PARCIAL;
            } else {
                status = StatusAcerto.QUITADO;
            }
            views.add(new AcertoAdminView(acerto, pago, status));
        }

        return views.reversed();
    }

}
