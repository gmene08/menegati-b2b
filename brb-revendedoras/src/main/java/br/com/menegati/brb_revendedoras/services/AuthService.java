package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.dto.auth.LoginRequestDTO;
import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.exception.ConflictException;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public void login(LoginRequestDTO userData, HttpServletResponse response) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(userData.login(), userData.password());

        var authentication = authenticationManager.authenticate(usernamePassword);

        var user = (User) authentication.getPrincipal();

        assert user != null;
        var token = jwtService.generateToken(user);

        response.addHeader(HttpHeaders.SET_COOKIE,buildCookie(token).toString());

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

        newUser.setCpf(userData.cpf());
        newUser.setName(userData.name());
        newUser.setEmail(userData.email());
        newUser.setPhone(userData.phone());
        newUser.setPassword(passwordEncoder.encode(userData.password()));
        newUser.setRole(role);
        newUser.setBirthDate(userData.birthDate().atStartOfDay());

        userRepository.save(newUser);
    }

    private ResponseCookie buildCookie(String token) {
        return ResponseCookie.from("token", token)
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(604800)
                .build();
    }
}
