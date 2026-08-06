package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.controller.AdminController;
import br.com.menegati.brb_revendedoras.controller.AdminController.PagamentoResponseDTO;
import br.com.menegati.brb_revendedoras.dto.revendedora.ExtratoResponseDTO;
import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.entity.Pagamento;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.FormaPagamento;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.mapper.ExtratoMapper;
import br.com.menegati.brb_revendedoras.repository.LancamentoFinanceiroRepository;
import br.com.menegati.brb_revendedoras.repository.PagamentoRepository;
import br.com.menegati.brb_revendedoras.repository.RevendedoraRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class ContaCorrenteService {
    private final RevendedoraRepository revendedoraRepository;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    private final LancamentoFinanceiroService lancamentoFinanceiroService;
    private final PagamentoRepository pagamentoRepository;
    private final ExtratoMapper extratoMapper;

    public BigDecimal getSaldoDevedor(Long revendedorId) {
        List<LancamentoFinanceiro> lancamentoFinanceiros = lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(revendedorId);
        return lancamentoFinanceiros.stream()
                .filter(l -> l.getCarteira() == CarteiraLancamento.DINHEIRO)
                .map(l ->{
                    if(l.getTipo() == TipoLancamento.DEBITO_ACERTO || l.getTipo() == TipoLancamento.AJUSTE_DEBITO){
                        return l.getValor();
                    }
                    else {
                        return l.getValor().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public PagamentoResponseDTO salvarPagamento(Long revendedoraId, float valorTotalPagamento, FormaPagamento formaPagamento, String observacao) {
        Revendedor revendedora = revendedoraRepository.findById(revendedoraId).orElseThrow(()-> new ResourceNotFoundException("Revendedora não encontrada"));
        // new BigDecimal(float) carrega o erro de arredondamento binário do float (ex: 250.0f vira 249.99999...);
        // valueOf passa por Float.toString antes, preservando o valor decimal digitado.
        BigDecimal valorTotal = BigDecimal.valueOf(valorTotalPagamento);

        if(valorTotal.compareTo(getSaldoDevedor(revendedoraId)) > 0){
            throw new BusinessException("Revendedora deve ter divida suficiente para realizar o pagamento.");
        }

        Pagamento pagamento = Pagamento.builder()
                .revendedor(revendedora)
                .valor(valorTotal)
                .dataPagamento(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()))
                .formaPagamento(formaPagamento)
                .observacao(observacao)
                .registradoPor(null)
                .build();

        pagamentoRepository.save(pagamento);

        lancamentoFinanceiroService.criarLancamento(pagamento);

        return new PagamentoResponseDTO("Pagamento de R$" + pagamento.getValor() +
                " da revendedora " + pagamento.getRevendedor().getName() +
                " realizado com sucesso.");

    }

    List<LancamentoFinanceiro> getHistoricoFinanceiro(Long revendedorId) {
        return lancamentoFinanceiroRepository.findByRevendedorIdOrderByDataAsc(revendedorId);
    }


    public ExtratoResponseDTO getExtrato(Long id) {
        List<LancamentoFinanceiro> lancamentos = lancamentoFinanceiroRepository.findByRevendedorIdAndCarteiraOrderByDataAsc(id, CarteiraLancamento.DINHEIRO);
        ExtratoResponseDTO extratoResponseDTO = new ExtratoResponseDTO();
        extratoResponseDTO.setLancamentos(extratoMapper.toLancamentoFinanceiroDTOList(lancamentos));
        return extratoResponseDTO;
    }
}
