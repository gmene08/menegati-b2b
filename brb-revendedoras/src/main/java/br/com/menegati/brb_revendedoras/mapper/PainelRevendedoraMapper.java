package br.com.menegati.brb_revendedoras.mapper;

import br.com.menegati.brb_revendedoras.dto.revendedora.PainelRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.entity.ItemConsignacao;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PainelRevendedoraMapper {

    @Mapping(target = "status", source = "statusItem")
    @Mapping(target = "codigo", source ="produto.codigo")
    @Mapping(target = "produto", source = "produto.nome")
    PainelRevendedoraResponseDTO.ItemDTO toItemDTO(ItemConsignacao entity);
    List<PainelRevendedoraResponseDTO.ItemDTO> toItemDTOList(List<ItemConsignacao> entities);

    @Mapping(target = "itens", source = "itens")
    PainelRevendedoraResponseDTO.LoteDTO toLoteDTO(LoteConsignacao entity);

    @Mapping(target = "loteId", source = "lote.id")
    PainelRevendedoraResponseDTO.DocumentoMaletaDTO toDocumentoDTO(DocumentoMaleta entity);
    List<PainelRevendedoraResponseDTO.DocumentoMaletaDTO> toDocumentoDTOList(List<DocumentoMaleta> entities);

    @Mapping(target = "nome", source = "name")
    PainelRevendedoraResponseDTO.RevendedorDTO toRevendedorDTO(Revendedor entity);
}
