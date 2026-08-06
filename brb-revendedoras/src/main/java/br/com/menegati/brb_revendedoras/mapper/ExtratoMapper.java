package br.com.menegati.brb_revendedoras.mapper;

import br.com.menegati.brb_revendedoras.dto.revendedora.ExtratoResponseDTO;
import br.com.menegati.brb_revendedoras.entity.LancamentoFinanceiro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ExtratoMapper {

    @Mapping(target = "acertoId", source = "acerto.id")
    @Mapping(target = "revendedora", source = "revendedor.name")
    ExtratoResponseDTO.LancamentoFinanceiroDTO toLancamentoFinanceiroDTO(LancamentoFinanceiro entity);
    List<ExtratoResponseDTO.LancamentoFinanceiroDTO> toLancamentoFinanceiroDTOList(List<LancamentoFinanceiro> entities);
}
