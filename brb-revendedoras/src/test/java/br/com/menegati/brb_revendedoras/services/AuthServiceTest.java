package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.PasswordResetToken;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.exception.ConflictException;
import br.com.menegati.brb_revendedoras.exception.ForbiddenException;
import br.com.menegati.brb_revendedoras.repository.PasswordResetTokenRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:4200");
    }

    @Test
    @DisplayName("Deve registrar um CLIENTE com sucesso e encriptar a senha")
    void deveRegistrarUmClienteComSucesso() {

        RegisterRequestDTO dto = new RegisterRequestDTO(
                "12345678900", "Senha123!", "João Cliente",
                "joao@email.com", "99999999", LocalDate.now()
        );

        when(userRepository.existsByCpf(dto.cpf())).thenReturn(false);

        when(passwordEncoder.encode(dto.password())).thenReturn("senhaEncriptada");

        authService.register(dto, Role.CLIENTE);

        verify(userRepository, times(1)).save(userCaptor.capture());

        User user = userCaptor.getValue();
        assertTrue(user instanceof Cliente, "Usuario salvo deve ser do tipo CLIENTE");
        assertEquals(dto.cpf(), user.getCpf(), "CPF do usuario salvo deve ser igual ao informado no DTO");
        assertEquals(dto.name(), user.getName(), "Nome do usuario salvo deve ser igual ao informado no DTO");
        assertEquals("senhaEncriptada", user.getPassword(), "Senha do usuario salvo deve ser igual a senha encriptada");
        assertEquals(Role.CLIENTE, user.getRole(), "Role do usuario salvo deve ser igual a ROLE_CLIENTE");

    }

    @Test
    @DisplayName("Deve registrar um REVENDEDOR com sucesso e encriptar a senha")
    void deveRegistrarUmRevendedorComSucesso() {

        RegisterRequestDTO dto = new RegisterRequestDTO(
                "11122233344", "Senha123!", "Eucineia Revendedora",
                "euci@email.com", "88888888", LocalDate.now()
        );

        when(userRepository.existsByCpf(dto.cpf())).thenReturn(false);

        when(passwordEncoder.encode(dto.password())).thenReturn("senhaEncriptada");

        authService.register(dto, Role.REVENDEDOR);

        verify(userRepository, times(1)).save(userCaptor.capture());

        User user = userCaptor.getValue();
        assertInstanceOf(Revendedor.class, user, "Usuario salvo deve ser do tipo REVENDEDOR");
        assertEquals(dto.cpf(), user.getCpf(), "CPF do usuario salvo deve ser igual ao informado no DTO");
        assertEquals(dto.name(), user.getName(), "Nome do usuario salvo deve ser igual ao informado no DTO");
        assertEquals("senhaEncriptada", user.getPassword(), "Senha do usuario salvo deve ser igual a senha encriptada");
        assertEquals(Role.REVENDEDOR, user.getRole(), "Role do usuario salvo deve ser igual a ROLE_REVENDEDOR");

    }

    @Test
    @DisplayName("Deve lancar excecao ao registrar com um cpf ja existente")
    void deveLancarExcecaoAoRegistrarComCpfJaExistente(){

        RegisterRequestDTO dto = new RegisterRequestDTO(
                "12345678900", "senha", "Copião", "copia@email", "000", LocalDate.now()
        );

        when(userRepository.existsByCpf(dto.cpf())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(dto, Role.CLIENTE));
        assertEquals("CPF já registrado", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve gerar token e enviar email quando o usuario existir ao solicitar recuperacao de senha")
    void deveEnviarEmailAoSolicitarRecuperacaoDeSenhaComUsuarioExistente() {

        String emailOuCpf = "12345678900";

        Cliente user = new Cliente();
        user.setId(1L);
        user.setEmail("joao@email.com");

        when(userRepository.findByCpfOrEmail(emailOuCpf, emailOuCpf)).thenReturn(Optional.of(user));
        when(passwordResetService.createPasswordResetToken(user)).thenReturn("token-abc-123");

        authService.forgotPassword(emailOuCpf);

        verify(emailService, times(1)).sendPasswordResetEmail(eq(user.getEmail()), stringCaptor.capture());

        String resetUrl = stringCaptor.getValue();
        assertTrue(resetUrl.contains("/redefinir-senha"), "URL deve apontar para a pagina de redefinicao de senha");
        assertTrue(resetUrl.contains("token=token-abc-123"), "URL deve conter o token gerado como query param");
    }

    @Test
    @DisplayName("Nao deve gerar token nem enviar email quando o usuario nao existir ao solicitar recuperacao de senha")
    void naoDeveEnviarEmailAoSolicitarRecuperacaoDeSenhaComUsuarioInexistente() {

        String emailOuCpf = "naoexiste@email.com";

        when(userRepository.findByCpfOrEmail(emailOuCpf, emailOuCpf)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> authService.forgotPassword(emailOuCpf));

        verify(passwordResetService, never()).createPasswordResetToken(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    @DisplayName("Deve lancar excecao ao redefinir senha com token inexistente")
    void deveLancarExcecaoAoRedefinirSenhaComTokenInexistente() {

        String token = "token-invalido";

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> authService.changePassword(token, "NovaSenha123!"));
        assertEquals("Código expirado ou invalido", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao ao redefinir senha com token expirado ou ja utilizado")
    void deveLancarExcecaoAoRedefinirSenhaComTokenExpiradoOuUtilizado() {

        String token = "token-expirado";
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(new Cliente());

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordResetService.isTokenValid(resetToken)).thenReturn(false);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> authService.changePassword(token, "NovaSenha123!"));
        assertEquals("Código expirado ou invalido", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao ao redefinir senha com uma senha fora do padrao exigido")
    void deveLancarExcecaoAoRedefinirSenhaComSenhaFracaDemais() {

        String token = "token-valido";
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(new Cliente());

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordResetService.isTokenValid(resetToken)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.changePassword(token, "fraca"));
        assertEquals("Senha deve conter de 8 a 30 caracteres, número, letra maiúscula e carácter especial", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve redefinir a senha com sucesso quando o token for valido e a senha atender aos requisitos")
    void deveRedefinirSenhaComSucesso() {

        String token = "token-valido";
        String novaSenha = "NovaSenha123!";

        Cliente user = new Cliente();
        user.setId(1L);
        user.setPassword("senhaAntigaEncriptada");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordResetService.isTokenValid(resetToken)).thenReturn(true);
        when(passwordEncoder.encode(novaSenha)).thenReturn("novaSenhaEncriptada");

        authService.changePassword(token, novaSenha);

        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("novaSenhaEncriptada", savedUser.getPassword(), "Senha do usuario deve ser atualizada com a senha encriptada");
    }

    @Test
    @DisplayName("Deve delegar a construção do cookie para o JwtService, que é quem sabe decodificar o token")
    void deveDelegarConstrucaoDoCookieParaOJwtService() {
        String token = "token-teste";
        ResponseCookie cookieEsperado = ResponseCookie.from("token", token).maxAge(30 * 24 * 60 * 60).build();
        when(jwtService.buildCookie(token)).thenReturn(cookieEsperado);

        ResponseCookie cookie = authService.buildCookie(token);

        assertSame(cookieEsperado, cookie, "AuthService.buildCookie deve repassar exatamente o cookie construído pelo JwtService");
    }
}
