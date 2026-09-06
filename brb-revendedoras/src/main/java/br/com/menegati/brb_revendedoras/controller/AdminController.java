package br.com.menegati.brb_revendedoras.controller;

import br.com.menegati.brb_revendedoras.dto.admin.PainelAdminResponseDTO;
import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.dto.revendedora.ExtratoResponseDTO;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.enums.FormaPagamento;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final EstoqueService estoqueService;
    private final LoteService loteService;
    private final ContaCorrenteService contaCorrenteService;
    private final PainelAdminService painelAdminService;

    public record ImportarEstoqueResponseDTO(String message, List<Long> linhasIgnoradas, int quantidadeLinhas) {}

    public record ImportarLoteResponseDTO(
            String message,
            Long loteId,
            String revendedora,
            int totalPecasCarregadasNestePdf,
            int totalPecasDaMaleta,
            float valorTotalEstimadoNestePdf,
            float valorTotalEstimadoDaMaleta,
            Map<Long,String> produtosIgnorados) {}

    public record AcertarLoteResponseDTO(
            String message,
            Long loteId,
            String revendedora,
            int totalPecasAcertadasNestePdf,
            int totalPecasDaMaleta,
            float valorTotalAcertadoNestePdf,
            float valorTotalAcertadoDaMaleta,
            Map<Long,String> produtosComAlerta) {}

    public record PagamentoRequestDTO(float valorTotalPagamento, FormaPagamento formaPagamento, String observacao) {}
    public record PagamentoResponseDTO(String message) {}

    public record SaldoResponseDTO(float saldo) {}

    @GetMapping
    public ResponseEntity<PainelAdminResponseDTO> getPainelAdminData(){
        PainelAdminResponseDTO responseDTO = painelAdminService.getPainelAdminData();
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/register/{role}")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO registerData, @PathVariable String role){
        authService.register(registerData, Role.valueOf(role.toUpperCase()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/estoque/importar")
    public ResponseEntity<ImportarEstoqueResponseDTO> registerEstoque(@RequestParam("file") MultipartFile file, Principal principal){

        if(file.isEmpty()){
            throw new BusinessException("O arquivo enviado está vazio");
        }

        if(!Objects.equals(file.getContentType(), "text/csv")){
            throw new BusinessException("Apenas arquivos .csv sao importados");
        }

        List<Long> linhasIgnoradas = estoqueService.registrarEstoque(file, principal.getName());
        if(CollectionUtils.isEmpty(linhasIgnoradas)){
            return ResponseEntity.ok(new ImportarEstoqueResponseDTO(
                    "Todos os produtos foram importados com sucesso",
                    linhasIgnoradas,
                    0));
        } else{
            return ResponseEntity.ok(new ImportarEstoqueResponseDTO(
                    "Importação concluida, mas algumas linhas continham erros e foram puladas.",
                    linhasIgnoradas,
                    linhasIgnoradas.size()));
        }
    }

    @PostMapping("/lote/importar")
    public ResponseEntity<ImportarLoteResponseDTO> registrarCargaLote(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            throw new BusinessException("O arquivo de carga enviado está vazio.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new BusinessException("Apenas arquivos em formato PDF são suportados para o importe de carga da maleta.");
        }

        LoteService.ProcessamentoLoteResult result = loteService.importarCargaMaletaPdf(file);
        LoteConsignacao lote = result.lote();

        return ResponseEntity.ok(new ImportarLoteResponseDTO("Maleta virtual carregada com sucesso!",
                lote.getId(),
                lote.getRevendedor().getName(),
                result.numeroDePecasDestePdf(),
                lote.getItens().size(),
                result.valorTotalDestePdf().floatValue(),
                lote.getValorTotalEstimado().floatValue(),
                result.produtosAlertas()
        ));
    }

    @PostMapping("/lote/acertar")
    public ResponseEntity<AcertarLoteResponseDTO> acertarCargaLote(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            throw new BusinessException("O arquivo de acerto enviado está vazio.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new BusinessException("Apenas arquivos em formato PDF são suportados para o acerto de carga da maleta.");
        }

        LoteService.ProcessamentoLoteResult result = loteService.acertarCargaMaletaPdf(file);
        LoteConsignacao lote = result.lote();

        return ResponseEntity.ok(new AcertarLoteResponseDTO("Carga acertada com sucesso!",
                lote.getId(),
                lote.getRevendedor().getName(),
                result.numeroDePecasDestePdf(),
                lote.getItens().size(),
                result.valorTotalDestePdf().floatValue(),
                lote.getValorTotalAcertado().floatValue(),
                result.produtosAlertas()
        ));
    }

    @PostMapping("/revendedora/{id}/pagamentos")
    public ResponseEntity<PagamentoResponseDTO> salvarPagamento(@PathVariable Long id, @RequestBody PagamentoRequestDTO pagamentoRequest){
        PagamentoResponseDTO responseDTO = contaCorrenteService.salvarPagamento(
                id,
                pagamentoRequest.valorTotalPagamento,
                pagamentoRequest.formaPagamento,
                pagamentoRequest.observacao
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/revendedora/{id}/saldo")
    public ResponseEntity<SaldoResponseDTO> getSaldo(@PathVariable Long id){
        SaldoResponseDTO responseDTO = new SaldoResponseDTO(contaCorrenteService.getSaldoDevedor(id).floatValue());

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/revendedora/{id}/extrato")
    public ResponseEntity<ExtratoResponseDTO> getExtrato(@PathVariable Long id){
        ExtratoResponseDTO responseDTO = contaCorrenteService.getExtrato(id);
        return ResponseEntity.ok(responseDTO);
    }

}
