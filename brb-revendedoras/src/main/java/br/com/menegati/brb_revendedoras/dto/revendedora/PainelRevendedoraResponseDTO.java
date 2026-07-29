package br.com.menegati.brb_revendedoras.dto.revendedora;

import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PainelRevendedoraResponseDTO {

    public record RevendedorDTO(String nome,
                                String email,
                                BigDecimal metaMensal,
                                BigDecimal valorDevidoAtual) {}

    public record ItemDTO(String codigo,
                          String produto,
                          StatusItemLote status,
                          BigDecimal valorUnitarioCongelado,
                          Integer quantidade) {}

    public record LoteDTO(StatusLote status,
                          String dataAbertura,
                          BigDecimal valorTotalEstimado,
                          BigDecimal valorTotalAcertado,
                          List<ItemDTO> itens) {}

    public record DocumentoMaletaDTO(String numeroConsignacao,
                                     String dataProcessamento,
                                     TipoDocumento tipoDocumento,
                                     BigDecimal valorTotal,
                                     Integer quantidadePecas,
                                     Long loteId) {}

    private RevendedorDTO perfil;
    private LoteDTO loteAtual;
    private List<DocumentoMaletaDTO> historicoDocumentos;


}
