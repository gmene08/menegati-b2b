package br.com.menegati.brb_revendedoras.controller;

import br.com.menegati.brb_revendedoras.dto.auth.LoginRequestDTO;
import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    public record LoginResponseDTO(String name, String role) {}

    public record ForgotPasswordResponseDTO(String message) {}
    public record ResetPasswordResponseDTO(String message) {}
    public record ResetPasswordRequestDTO(String token, String newPassword) {}

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO registerData){
        this.authService.register(registerData, Role.CLIENTE);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginData, HttpServletResponse response){
        LoginResponseDTO responseDTO = authService.login(loginData, response);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@RequestBody Map<String, String> userData){
        authService.forgotPassword(userData.get("emailOrCpf"));
        return ResponseEntity.ok(new ForgotPasswordResponseDTO("Se existir uma conta associada aos dados informados, enviaremos as instruções por e-mail."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@RequestBody ResetPasswordRequestDTO resetData){
        authService.changePassword(resetData.token(), resetData.newPassword());
        return ResponseEntity.ok(new ResetPasswordResponseDTO("Senha alterada com sucesso."));
    }
}
