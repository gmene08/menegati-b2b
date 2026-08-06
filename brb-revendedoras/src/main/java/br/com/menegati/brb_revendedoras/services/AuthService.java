package br.com.menegati.brb_revendedoras.services;


import br.com.menegati.brb_revendedoras.dto.auth.LoginRequestDTO;
import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.PasswordResetToken;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.enums.SessionDuration;
import br.com.menegati.brb_revendedoras.exception.*;
import br.com.menegati.brb_revendedoras.controller.AuthController.LoginResponseDTO;
import br.com.menegati.brb_revendedoras.repository.PasswordResetTokenRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import br.com.menegati.brb_revendedoras.security.PasswordValidatorUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public LoginResponseDTO validateSession(String cpf){
        User user = userRepository.findByCpf(cpf).orElseThrow(()->new ResourceNotFoundException("Usuário não encontrado"));
        return new LoginResponseDTO(user.getName(), user.getRole().name());
    }

    public LoginResponseDTO login(LoginRequestDTO userData, HttpServletResponse response) {

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(userData.login(), userData.password());

            var authentication = authenticationManager.authenticate(usernamePassword);

            var user = (User) authentication.getPrincipal();

            Objects.requireNonNull(user);

            SessionDuration sessionDuration = SessionDuration.fromRememberMe(userData.rememberMe());

            var token = jwtService.generateToken(user, sessionDuration.getJwtExpiration(), userData.rememberMe());

            response.addHeader(HttpHeaders.SET_COOKIE,buildCookie(token).toString());

            return new LoginResponseDTO(user.getName(), user.getRole().name());
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Login ou senha inválidos. Verifique e tente novamente.");
        }

    }

    @Transactional
    public void register(RegisterRequestDTO userData, Role role){
        if(userRepository.existsByCpf(userData.cpf())){
            throw new ConflictException("CPF já registrado");
        }

        User newUser;
        if(role == Role.REVENDEDOR){
            // campos especificos para revendedores
            newUser = new Revendedor();
        }
        else if(role == Role.CLIENTE){
             // campos especificos para clientes
            newUser = new Cliente();
        }else {
             throw new RuntimeException("Role não reconhecida");
        }

        this.validatePassword(userData.password());

        newUser.setCpf(userData.cpf());
        newUser.setName(userData.name());
        newUser.setEmail(userData.email());
        newUser.setPhone(userData.phone());
        newUser.setPassword(passwordEncoder.encode(userData.password()));
        newUser.setRole(role);
        newUser.setBirthDate(userData.birthDate().atStartOfDay());

        userRepository.save(newUser);
    }

    public void forgotPassword(String emailOrCpf){
        User user = userRepository.findByCpfOrEmail(emailOrCpf, emailOrCpf).orElse(null);

        if(user == null){
            return;
        }

        String token = passwordResetService.createPasswordResetToken(user);

        String resetUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/redefinir-senha")
                .queryParam("token", token)
                .build()
                .toUriString();

        emailService.sendPasswordResetEmail(user.getEmail(),  resetUrl);
    }

    public void changePassword(String token, String newPassword){
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(()->new ForbiddenException("Código expirado ou invalido"));

        if(!passwordResetService.isTokenValid(passwordResetToken)){
            throw new ForbiddenException("Código expirado ou invalido");
        }

        this.validatePassword(newPassword);
        passwordResetToken.setUsed(true);

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void logout(HttpServletResponse response){
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("").toString());
    }

    public ResponseCookie buildCookie(String token) {
        return jwtService.buildCookie(token);
    }

    private void validatePassword(String password){
        if(!PasswordValidatorUtil.isValid(password)){
            throw new BusinessException("Senha deve conter de 8 a 30 caracteres, número, letra maiúscula e carácter especial");
        }
    }
}
