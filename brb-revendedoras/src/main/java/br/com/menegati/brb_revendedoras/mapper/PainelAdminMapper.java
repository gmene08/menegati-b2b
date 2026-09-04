package br.com.menegati.brb_revendedoras.mapper;

import br.com.menegati.brb_revendedoras.dto.admin.PainelAdminResponseDTO;
import br.com.menegati.brb_revendedoras.entity.*;
import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import br.com.menegati.brb_revendedoras.services.PainelAdminService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",imports = OrigemProcessamento.class ,  unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PainelAdminMapper {

    @Mapping(target = "id",       source = "revendedor.id")
    @Mapping(target = "nome",     source = "revendedor.name")
    @Mapping(target = "cpf",      source = "revendedor.cpf")
    @Mapping(target = "telefone", source = "revendedor.phone")
    PainelAdminResponseDTO.AdminRevendedoraDTO toAdminRevendedoraDTO(PainelAdminService.AdminRevendedoraView view);
    List<PainelAdminResponseDTO.AdminRevendedoraDTO> toAdminRevendedoraDTOList(List<PainelAdminService.AdminRevendedoraView> views);


    PainelAdminResponseDTO.ProdutoDTO toProdutoDTO(Produto entity);
    List<PainelAdminResponseDTO.ProdutoDTO> toProdutoDTOList(List<Produto> entities);

    @Mapping(target = "tipo", constant = "CARGA")
    @Mapping(target = "origem", expression = "java(c.getDocumentoMaleta() != null ? OrigemProcessamento.PDF : OrigemProcessamento.MANUAL)")
    @Mapping(target = "revendedora", source = "revendedor.name")
    @Mapping(target = "numeroDocumento", source = "documentoMaleta.numeroConsignacao")
    @Mapping(target = "dataProcessamento", source = "dataAbertura")
    @Mapping(target = "quantidadeItens", source = "quantidadePecas")
    @Mapping(target = "valorTotal", source = "valorTotalEstimado")
    PainelAdminService.ProcessamentoView toView(CargaConsignacao c);

    @Mapping(target = "tipo", constant = "ACERTO")
    @Mapping(target = "origem", expression = "java(a.getDocumentoMaleta() != null ? OrigemProcessamento.PDF : OrigemProcessamento.MANUAL)")
    @Mapping(target = "revendedora", source = "revendedor.name")
    @Mapping(target = "numeroDocumento", source = "documentoMaleta.numeroConsignacao")
    @Mapping(target = "dataProcessamento", source = "dataAcerto")
    @Mapping(target = "quantidadeItens", source = "qtdPecasVendidas")
    @Mapping(target = "valorTotal", source = "valorVendidoBruto")
    PainelAdminService.ProcessamentoView toView(Acerto a);

    @Mapping(target = "tipo", constant = "ESTOQUE")
    @Mapping(target = "origem", expression = "java(e.getDocumentoEstoque() != null ? OrigemProcessamento.CSV : OrigemProcessamento.MANUAL)")
    @Mapping(target = "revendedora", ignore = true)
    @Mapping(target = "numeroDocumento", ignore = true)
    @Mapping(target = "dataProcessamento", source = "dataEntrada")
    @Mapping(target = "quantidadeItens", expression = "java(e.getItens().size())")
    @Mapping(target = "valorTotal", constant = "0")
    PainelAdminService.ProcessamentoView toView(EntradaEstoque e);

    PainelAdminResponseDTO.ProcessamentoDTO toProcessamentoDTO(PainelAdminService.ProcessamentoView view);
    List<PainelAdminResponseDTO.ProcessamentoDTO> toProcessamentoDTOList(List<PainelAdminService.ProcessamentoView> views);
}
