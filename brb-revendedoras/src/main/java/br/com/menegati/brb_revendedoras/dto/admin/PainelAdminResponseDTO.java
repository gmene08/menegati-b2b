package br.com.menegati.brb_revendedoras.dto.admin;

import br.com.menegati.brb_revendedoras.enums.OrigemProcessamento;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.enums.TipoProcessamento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PainelAdminResponseDTO {

    public record AdminRevendedoraDTO(Long id, String nome, String cpf, String telefone,
                                      BigDecimal saldoDevedor, BigDecimal saldoBonus,
                                      BigDecimal exposicaoMaleta, int diasAtraso,
                                      LocalDate dataUltimoAcerto, StatusLote loteStatus) {}

    public record ProdutoDTO(String codigo,
                          String nome,
                          BigDecimal precoVenda,
                          Integer quantidadeDisponivel) {}

    public record ProcessamentoDTO(Long id, TipoProcessamento tipo, OrigemProcessamento origem,
                                   String revendedora, LocalDateTime dataProcessamento,
                                   String numeroDocumento, int quantidadeItens,
                                   BigDecimal valorTotal) {}

    private List<AdminRevendedoraDTO> revendedoras;
    private List<ProdutoDTO> estoque;
    private List<ProcessamentoDTO> processamentos;
}
