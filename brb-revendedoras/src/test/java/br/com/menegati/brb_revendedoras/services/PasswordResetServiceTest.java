package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.PasswordResetToken;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.repository.PasswordResetTokenRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    @Test
    @DisplayName("Deve criar um novo token de recuperacao quando o usuario nao possuir um")
    void deveCriarNovoTokenQuandoUsuarioNaoPossuiToken() {

        User user = new Cliente();
        user.setId(1L);

        when(passwordResetTokenRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        String token = passwordResetService.createPasswordResetToken(user);

        assertNotNull(token, "Token gerado nao deve ser nulo");

        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(token, savedToken.getToken(), "Token salvo deve ser igual ao token retornado");
        assertEquals(user, savedToken.getUser(), "Usuario do token salvo deve ser o mesmo informado");
        assertTrue(savedToken.getExpirationDate().isAfter(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId())), "Data de expiracao deve ser no futuro");
    }

    @Test
    @DisplayName("Deve reaproveitar o token existente do usuario, atualizando valor e expiracao")
    void deveReaproveitarTokenExistenteDoUsuario() {

        User user = new Cliente();
        user.setId(1L);

        PasswordResetToken tokenExistente = new PasswordResetToken();
        tokenExistente.setToken("token-antigo");
        tokenExistente.setUser(user);
        tokenExistente.setExpirationDate(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()).minusMinutes(5));
        tokenExistente.setUsed(true);

        when(passwordResetTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(tokenExistente));

        String novoToken = passwordResetService.createPasswordResetToken(user);

        assertNotEquals("token-antigo", novoToken, "Um novo valor de token deve ser gerado");

        verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertSame(tokenExistente, savedToken, "Deve reaproveitar a mesma entidade de token ja existente");
        assertEquals(novoToken, savedToken.getToken(), "Token salvo deve ter sido atualizado com o novo valor");
        assertTrue(savedToken.getExpirationDate().isAfter(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId())), "Data de expiracao deve ter sido renovada para o futuro");
    }

    @Test
    @DisplayName("Deve considerar valido um token que nao esta expirado nem utilizado")
    void deveConsiderarTokenValido() {

        PasswordResetToken token = new PasswordResetToken();
        token.setExpirationDate(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()).plusMinutes(10));
        token.setUsed(false);

        assertTrue(passwordResetService.isTokenValid(token));
    }

    @Test
    @DisplayName("Deve considerar invalido um token expirado")
    void deveConsiderarTokenInvalidoQuandoExpirado() {

        PasswordResetToken token = new PasswordResetToken();
        token.setExpirationDate(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()).minusMinutes(1));
        token.setUsed(false);

        assertFalse(passwordResetService.isTokenValid(token));
    }

    @Test
    @DisplayName("Deve considerar invalido um token ja utilizado")
    void deveConsiderarTokenInvalidoQuandoJaUtilizado() {

        PasswordResetToken token = new PasswordResetToken();
        token.setExpirationDate(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()).plusMinutes(10));
        token.setUsed(true);

        assertFalse(passwordResetService.isTokenValid(token));
    }
}
