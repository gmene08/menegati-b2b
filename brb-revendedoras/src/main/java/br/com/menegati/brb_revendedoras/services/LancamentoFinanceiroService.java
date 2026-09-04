package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import br.com.menegati.brb_revendedoras.entity.Pagamento;
import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import br.com.menegati.brb_revendedoras.repository.LancamentoFinanceiroRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LancamentoFinanceiroService {

    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    @Transactional
    public void criarLancamento(Acerto acerto){

        LancamentoFinanceiro lancamentoFinanceiro = LancamentoFinanceiro.builder()
                .revendedor(acerto.getRevendedor())
                .data(acerto.getDataAcerto())
                .tipo(TipoLancamento.DEBITO_ACERTO)
                .carteira(CarteiraLancamento.DINHEIRO)
                .valor(acerto.getValorDevido())
                .descricao(criarDescricao(acerto))
                .acerto(acerto)
                .pagamentoId(null)
                .criadoPor(null)
                .observacao(null)
                .build();

        lancamentoFinanceiroRepository.save(lancamentoFinanceiro);
    }

    @Transactional
    public void criarLancamento(Pagamento pagamento){

        LancamentoFinanceiro lancamentoFinanceiro = LancamentoFinanceiro.builder()
                .revendedor(pagamento.getRevendedor())
                .data(pagamento.getDataPagamento())
                .tipo(TipoLancamento.CREDITO_PAGAMENTO)
                .carteira(CarteiraLancamento.DINHEIRO)
                .valor(pagamento.getValor().negate()) // Valor negativo para creditar
                .descricao(criarDescricao(pagamento))
                .acerto(null)
                .pagamentoId(pagamento.getId())
                .criadoPor(null)
                .observacao(null)
                .build();

        lancamentoFinanceiroRepository.save(lancamentoFinanceiro);
    }

    private String criarDescricao(Acerto acerto){
        String infoDocumento = acerto.getDocumentoMaleta() != null
                ? "Documento de acerto: " + acerto.getDocumentoMaleta().getNumeroConsignacao() + "\n"
                : "Acerto feito de forma manual (sem documento)\n";

        String descricao = """
                Lançamento de ACERTO para revendedor %s.
                Data: %s
                Valor: %s
                %s""";

        return descricao.formatted(acerto.getRevendedor().getName(), acerto.getDataAcerto(), acerto.getValorDevido(), infoDocumento);

    }

    private String criarDescricao(Pagamento pagamento){
        String descricao = """
                Lançamento de PAGAMENTO para revendedor %s.
                Data: %s
                Valor: %s""";

        return descricao.formatted(pagamento.getRevendedor().getName(), pagamento.getDataPagamento(), pagamento.getValor());
    }

}
