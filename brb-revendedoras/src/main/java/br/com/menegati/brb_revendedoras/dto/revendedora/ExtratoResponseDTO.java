package br.com.menegati.brb_revendedoras.dto.revendedora;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtratoResponseDTO {
    public record LancamentoFinanceiroDTO(
            String revendedora,
            String data,
            Long acertoId,
            String tipo,
            String valor,
            String descricao,
            String observacao,
            String criadoPor
    ){}

    private List<LancamentoFinanceiroDTO> lancamentos;
}
