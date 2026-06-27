package br.com.menegati.brb_revendedoras.controller;

import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PostMapping("/register/{role}")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO registerData, @PathVariable String role){
        authService.register(registerData, Role.valueOf(role.toUpperCase()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
