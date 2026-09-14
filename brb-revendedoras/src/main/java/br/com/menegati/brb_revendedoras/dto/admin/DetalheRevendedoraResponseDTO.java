package br.com.menegati.brb_revendedoras.dto.admin;

import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.StatusAcerto;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DetalheRevendedoraResponseDTO {

    public record LancamentoDTO(Long id, String data, TipoLancamento tipo, CarteiraLancamento carteira,
                                BigDecimal valor, String descricao, String observacao,
                                BigDecimal saldoApos) {}

    public record AcertoAdminDTO(Long id, String documentoMaleta, String dataAcerto, String dataVencimento,
                                 BigDecimal valorVendidoBruto, BigDecimal valorComissao, BigDecimal valorDevido,
                                 BigDecimal valorPago, StatusAcerto status) {}

    private PainelAdminResponseDTO.AdminRevendedoraDTO resumo;
    private List<LancamentoDTO> lancamentos;
    private List<AcertoAdminDTO> acertos;
}
