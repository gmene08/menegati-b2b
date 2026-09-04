package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.dto.admin.PainelAdminResponseDTO;
import br.com.menegati.brb_revendedoras.entity.CargaConsignacao;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.entity.ItemCarga;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.mapper.PainelAdminMapper;
import br.com.menegati.brb_revendedoras.repository.CargaConsignacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CargaService {

    private final CargaConsignacaoRepository cargaConsignacaoRepository;

    @Transactional
    public CargaConsignacao criarCarga(DocumentoMaleta documentoMaleta, List<ItemCarga> itens){
        if(!documentoMaleta.getTipoDocumento().equals(TipoDocumento.MALETA_ENTRADA)){
            throw new BusinessException("Para criar carga a partir de um documento de maleta, o tipo de documento deve ser MALETA_ENTRADA");
        }

        if(itens.isEmpty()){
            throw new BusinessException("Não é possível criar uma carga sem itens.");
        }

        BigDecimal valorTotalEstimado = itens.stream()
                .map(item -> item.getValorUnitarioCongelado().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int quantidadePecas = itens.stream().mapToInt(ItemCarga::getQuantidade).sum();

        CargaConsignacao carga = CargaConsignacao.builder()
                .documentoMaleta(documentoMaleta)
                .revendedor(documentoMaleta.getRevendedor())
                .lote(documentoMaleta.getLote())
                .dataAbertura(documentoMaleta.getDataProcessamento())
                .quantidadePecas(quantidadePecas)
                .valorTotalEstimado(valorTotalEstimado)
                .itens(itens)
                .build();

        itens.forEach(item -> item.setCarga(carga));

        return cargaConsignacaoRepository.save(carga);
    }

    public List<CargaConsignacao> getCargasParaFeed(){
        return cargaConsignacaoRepository.findAllByOrderByDataAberturaDesc(PageRequest.of(0, 20));
    }
}
