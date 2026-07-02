package br.com.menegati.brb_revendedoras.controller;

import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.services.AuthService;
import br.com.menegati.brb_revendedoras.services.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final EstoqueService estoqueService;

    public record ImportarEstoqueResponseDTO(String message, List<Long> linhasIgnoradas, int quantidadeLinhas) {}

    @PostMapping("/register/{role}")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO registerData, @PathVariable String role){
        authService.register(registerData, Role.valueOf(role.toUpperCase()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/estoque/importar")
    public ResponseEntity<ImportarEstoqueResponseDTO> registerEstoque(@RequestParam("file") MultipartFile file){

        if(file.isEmpty()){
            throw new BusinessException("O arquivo enviado está vazio");
        }

        if(!Objects.equals(file.getContentType(), "text/csv")){
            throw new BusinessException("Apenas arquivos .csv sao importados");
        }

        List<Long> linhasIgnoradas = estoqueService.registerEstoque(file);
        if(linhasIgnoradas.isEmpty()){
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

}
