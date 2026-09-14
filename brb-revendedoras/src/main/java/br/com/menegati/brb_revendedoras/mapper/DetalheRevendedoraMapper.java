package br.com.menegati.brb_revendedoras.mapper;

import br.com.menegati.brb_revendedoras.dto.admin.DetalheRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.services.PainelAdminService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DetalheRevendedoraMapper {

    @Mapping(target = "id",         source = "lancamento.id")
    @Mapping(target = "data",       source = "lancamento.data", dateFormat = "dd/MM/yyyy HH:mm")
    @Mapping(target = "tipo",       source = "lancamento.tipo")
    @Mapping(target = "carteira",   source = "lancamento.carteira")
    @Mapping(target = "valor",      source = "lancamento.valor")
    @Mapping(target = "descricao",  source = "lancamento.descricao")
    @Mapping(target = "observacao", source = "lancamento.observacao")
    DetalheRevendedoraResponseDTO.LancamentoDTO toLancamentoDTO(PainelAdminService.LancamentoView view);
    List<DetalheRevendedoraResponseDTO.LancamentoDTO> toLancamentoDTOList(List<PainelAdminService.LancamentoView> views);

    @Mapping(target = "id",                source = "acerto.id")
    @Mapping(target = "documentoMaleta",   expression = "java(formatarNomeDocumentoEmAcerto(view.acerto()))")
    @Mapping(target = "dataAcerto",        source = "acerto.dataAcerto", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "dataVencimento",    source = "acerto.dataVencimento", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "valorVendidoBruto", source = "acerto.valorVendidoBruto")
    @Mapping(target = "valorComissao",     source = "acerto.valorComissao")
    @Mapping(target = "valorDevido",       source = "acerto.valorDevido")
    DetalheRevendedoraResponseDTO.AcertoAdminDTO toAcertoAdminDTO(PainelAdminService.AcertoAdminView view);
    List<DetalheRevendedoraResponseDTO.AcertoAdminDTO> toAcertoAdminDTOList(List<PainelAdminService.AcertoAdminView> views);


    // --- Métodos Helper (Custom Logic) ---

    // Mesmo texto usado em PainelRevendedoraMapper, para admin e revendedora verem o mesmo rótulo.
    default String formatarNomeDocumentoEmAcerto(Acerto acerto) {
        if (acerto.getDocumentoMaleta() == null || acerto.getDocumentoMaleta().getNumeroConsignacao().isBlank()) {
            return "Foi feito manualmente (sem documento)";
        }
        return acerto.getDocumentoMaleta().getNumeroConsignacao();
    }
}
