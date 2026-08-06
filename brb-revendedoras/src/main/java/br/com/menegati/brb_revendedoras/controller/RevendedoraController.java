package br.com.menegati.brb_revendedoras.controller;

import br.com.menegati.brb_revendedoras.dto.revendedora.PainelRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.services.RevendedoraService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/revendedora")
@RequiredArgsConstructor
public class RevendedoraController {
   private final  RevendedoraService revendedoraService;

    @GetMapping("")
    public ResponseEntity<PainelRevendedoraResponseDTO> getPainelRevendedora(Principal principal) {
        PainelRevendedoraResponseDTO responseDTO = revendedoraService.getRevendedoraData(principal.getName());
        return ResponseEntity.ok(responseDTO);
    }


}
