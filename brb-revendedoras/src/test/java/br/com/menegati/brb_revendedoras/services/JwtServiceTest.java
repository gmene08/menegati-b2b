package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.Cliente;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp(){
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecretString", "test-secret-key");

        user = Cliente.builder()
                .name("Gab Cliente")
                .cpf("12345678900")
                .role(Role.CLIENTE)
                .build();
    }

    @Test
    @DisplayName("Deve gerar um token e validar retornando o CPF corretp")
    void deveGerarEValidarTokenComSucesso(){
        String token = jwtService.generateToken(user,Duration.ofHours(8), false);
        assertEquals(user.getCpf(), jwtService.validateToken(token));
    }

    @Test
    @DisplayName("Token gerado deve conter as claims corretas")
    void tokenGeradoDeveConterClaimsCorretas() {
        String token = jwtService.generateToken(user, Duration.ofDays(30), true);

        DecodedJWT decoded = JWT.decode(token); // decode() não verifica assinatura, só lê o payload

        assertEquals(user.getName(), decoded.getClaim("name").asString());
        assertEquals(user.getRole().name(), decoded.getClaim("role").asString());
        assertEquals(user.getCpf(), decoded.getSubject());
        assertTrue(decoded.getClaim("rememberMe").asBoolean());
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar um token expirado")
    void deveRetornarVazioParaTokenExpirado() throws InterruptedException {
        String token = jwtService.generateToken(user, Duration.ofMillis(50), false);
        Thread.sleep(100);

        assertEquals("", jwtService.validateToken(token));
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar um token com assinatura adulterada")
    void deveRetornarVazioParaTokenAdulterado() {
        String token = jwtService.generateToken(user, Duration.ofHours(8), false);
        String tokenAdulterado = token.substring(0, token.length() - 5) + "AAAAA";

        assertEquals("", jwtService.validateToken(tokenAdulterado));
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar token vazio")
    void deveRetornarVazioParaTokenVazio() {
        assertEquals("", jwtService.validateToken(""));
    }

    @Test
    @DisplayName("getRememberMe deve refletir a claim rememberMe gravada no token")
    void getRememberMeDeveRefletirClaimDoToken() {
        String tokenComRememberMe = jwtService.generateToken(user, Duration.ofDays(30), true);
        String tokenSemRememberMe = jwtService.generateToken(user, Duration.ofHours(8), false);

        assertTrue(jwtService.getRememberMe(tokenComRememberMe));
        assertFalse(jwtService.getRememberMe(tokenSemRememberMe));
    }

    @Test
    @DisplayName("buildCookie deve expirar imediatamente (maxAge 0) quando o token for vazio")
    void buildCookieDeveExpirarQuandoTokenForVazio() {
        ResponseCookie cookie = jwtService.buildCookie("");
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }

    @Test
    @DisplayName("buildCookie deve usar duração longa (30 dias) quando o token tiver rememberMe=true")
    void buildCookieDeveUsarDuracaoLongaQuandoRememberMeForTrue() {
        String token = jwtService.generateToken(user, Duration.ofDays(30), true);
        ResponseCookie cookie = jwtService.buildCookie(token);
        assertEquals(30 * 24 * 60 * 60, cookie.getMaxAge().getSeconds());
    }

    @Test
    @DisplayName("buildCookie deve expirar ao fechar o navegador (maxAge -1) quando o token tiver rememberMe=false")
    void buildCookieDeveExpirarAoFecharNavegadorQuandoRememberMeForFalse() {
        String token = jwtService.generateToken(user, Duration.ofHours(8), false);
        ResponseCookie cookie = jwtService.buildCookie(token);
        assertEquals(-1, cookie.getMaxAge().getSeconds());
    }
}
