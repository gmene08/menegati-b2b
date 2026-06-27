package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.dto.auth.RegisterRequestDTO;
import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.exception.ConflictException;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Deve registrar um CLIENTE com sucesso e encriptar a senha")
    void deveRegistrarUmClienteComSucesso() {

        RegisterRequestDTO dto = new RegisterRequestDTO(
                "12345678900", "senha123", "João Cliente",
                "joao@email.com", "99999999", LocalDateTime.now()
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
                "11122233344", "senha123", "Eucineia Revendedora",
                "euci@email.com", "88888888", LocalDateTime.now()
        );

        when(userRepository.existsByCpf(dto.cpf())).thenReturn(false);

        when(passwordEncoder.encode(dto.password())).thenReturn("senhaEncriptada");

        authService.register(dto, Role.REVENDEDOR);

        verify(userRepository, times(1)).save(userCaptor.capture());

        User user = userCaptor.getValue();
        assertTrue(user instanceof Revendedor, "Usuario salvo deve ser do tipo REVENDEDOR");
        assertEquals(dto.cpf(), user.getCpf(), "CPF do usuario salvo deve ser igual ao informado no DTO");
        assertEquals(dto.name(), user.getName(), "Nome do usuario salvo deve ser igual ao informado no DTO");
        assertEquals("senhaEncriptada", user.getPassword(), "Senha do usuario salvo deve ser igual a senha encriptada");
        assertEquals(Role.REVENDEDOR, user.getRole(), "Role do usuario salvo deve ser igual a ROLE_REVENDEDOR");

    }

    @Test
    @DisplayName("Deve lancar excecao ao registrar com um cpf ja existente")
    void deveLancarExcecaoAoRegistrarComCpfJaExistente(){

        RegisterRequestDTO dto = new RegisterRequestDTO(
                "12345678900", "senha", "Copião", "copia@email", "000", LocalDateTime.now()
        );

        when(userRepository.existsByCpf(dto.cpf())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(dto, Role.CLIENTE));
        assertEquals("CPF já registrado", exception.getMessage());

        verify(userRepository, never()).save(any());
    }
}
