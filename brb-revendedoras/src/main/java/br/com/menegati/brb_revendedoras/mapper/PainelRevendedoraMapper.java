package br.com.menegati.brb_revendedoras.mapper;

import br.com.menegati.brb_revendedoras.dto.revendedora.PainelRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PainelRevendedoraMapper {

    @Mapping(target = "status", source = "statusItem")
    @Mapping(target = "codigo", source ="produto.codigo")
    @Mapping(target = "produto", source = "produto.nome")
    PainelRevendedoraResponseDTO.ItemDTO toItemDTO(ItemLote entity);
    List<PainelRevendedoraResponseDTO.ItemDTO> toItemDTOList(List<ItemLote> entities);

    @Mapping(target = "itens", source = "itens")
    @Mapping(target = "dataAbertura", dateFormat = "dd/MM/yyyy")
    PainelRevendedoraResponseDTO.LoteDTO toLoteDTO(LoteConsignacao entity);

    @Mapping(target = "loteId", source = "lote.id")
    @Mapping(target = "dataProcessamento", dateFormat = "dd/MM/yyyy")
    PainelRevendedoraResponseDTO.DocumentoMaletaDTO toDocumentoDTO(DocumentoMaleta entity);
    List<PainelRevendedoraResponseDTO.DocumentoMaletaDTO> toDocumentoDTOList(List<DocumentoMaleta> entities);

    @Mapping(target = "loteId", source = "lote.id")
    @Mapping(target = "documentoMaleta", expression = "java(formatarNomeDocumentoEmAcerto(entity))")
    @Mapping(target = "dataAcerto", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "dataVencimento", dateFormat = "dd/MM/yyyy")
    PainelRevendedoraResponseDTO.AcertoDTO toAcertoDTO(Acerto entity);
    List<PainelRevendedoraResponseDTO.AcertoDTO> toAcertoDTOList(List<Acerto> entities);

    @Mapping(target = "loteId", source = "lote.id")
    @Mapping(target = "documentoMaleta", source = "documentoMaleta.numeroConsignacao")
    @Mapping(target = "dataAbertura", dateFormat = "dd/MM/yyyy")
    PainelRevendedoraResponseDTO.CargaDTO toCargaDTO(CargaConsignacao entity);
    List<PainelRevendedoraResponseDTO.CargaDTO> toCargaDTOList(List<CargaConsignacao> entities);

    @Mapping(target = "nome", source = "entity.name")
    @Mapping(target = "valorDevidoAtual", source = "saldoDevedor")
    PainelRevendedoraResponseDTO.RevendedorDTO toRevendedorDTO(Revendedor entity, BigDecimal saldoDevedor);


    // --- Métodos Helper (Custom Logic) ---


    default String formatarNomeDocumentoEmAcerto(Acerto acerto) {
        if (acerto.getDocumentoMaleta() == null || acerto.getDocumentoMaleta().getNumeroConsignacao().isBlank()) {
            return "Foi feito manualmente (sem documento)";
        }
        return acerto.getDocumentoMaleta().getNumeroConsignacao();
    }
}
