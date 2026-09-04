package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.repository.AcertoRepository;
import br.com.menegati.brb_revendedoras.repository.LancamentoFinanceiroRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcertoService {

    public record CalculoComissao(BigDecimal valorComissao, BigDecimal valorDevido){}

    private final AcertoRepository acertoRepository;
    private final LancamentoFinanceiroService lancamentoFinanceiroService;


    @Transactional
    public void criarAcerto(DocumentoMaleta documentoMaleta){
        if(!documentoMaleta.getTipoDocumento().equals(TipoDocumento.MALETA_ACERTO)){
            throw new BusinessException("Para criar acerto a partir de um documento de maleta de acerto, o tipo de documento deve ser MALETA_ACERTO");
        }

        LocalDate dataVencimento = calcularDataVencimento(documentoMaleta.getDataProcessamento().toLocalDate());
        CalculoComissao calculoComissao = calcularComissao(documentoMaleta.getValorTotal(), documentoMaleta.getRevendedor().getPercentualComissao());

        Acerto acerto = Acerto.builder()
                .documentoMaleta(documentoMaleta)
                .dataAcerto(documentoMaleta.getDataProcessamento())
                .valorVendidoBruto(documentoMaleta.getValorTotal())
                .revendedor(documentoMaleta.getRevendedor())
                .lote(documentoMaleta.getLote())
                .dataVencimento(dataVencimento)
                .percentualComissaoAplicado(documentoMaleta.getRevendedor().getPercentualComissao())
                .valorComissao(calculoComissao.valorComissao)
                .valorDevido(calculoComissao.valorDevido)
                .qtdPecasVendidas(documentoMaleta.getQuantidadePecas())
                .build();

        Acerto acertoSaved = acertoRepository.save(acerto);
        lancamentoFinanceiroService.criarLancamento(acertoSaved);
    }

    private CalculoComissao calcularComissao(BigDecimal valorVendidoBruto, BigDecimal percentualComissaoAplicado){
        BigDecimal valorComissao = valorVendidoBruto.multiply(percentualComissaoAplicado).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal valorDevido = valorVendidoBruto.subtract(valorComissao);
        return new CalculoComissao(valorComissao, valorDevido);

    }

    private LocalDate calcularDataVencimento(LocalDate dataAcerto){
        return dataAcerto.plusDays(30);
    }

    public Map<Long, LocalDateTime> getUltimasDatasDeAcerto(){
        return acertoRepository.getUltimaDataAcertoPorRevendedor().stream().collect(
                Collectors.toMap(
                        o -> (Long) o[0],
                        o -> (LocalDateTime) o[1]
                )
        );
    }

    public List<Acerto> getAcertosParaFeed(){
        return acertoRepository.findAllByOrderByDataAcertoDesc(PageRequest.of(0, 20));
    }
}
