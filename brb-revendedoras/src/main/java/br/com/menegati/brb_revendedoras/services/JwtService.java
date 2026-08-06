package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.SessionDuration;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${JWT_SECRET}")
    private String jwtSecretString;

    private Algorithm getSigningKey(){
        return Algorithm.HMAC256(jwtSecretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user, Duration jwtExpirationTimeInHours, boolean rememberMe){
        try{

            return JWT.create()
                    .withIssuer("menegati-b2b")
                    .withSubject(user.getCpf())
                    .withClaim("name", user.getName())
                    .withClaim("role", user.getRole().name())
                    .withClaim("rememberMe", rememberMe)
                    .withExpiresAt(calcExpirationDate(jwtExpirationTimeInHours))
                    .sign(getSigningKey());
        } catch (JWTCreationException e){
            throw new RuntimeException("error generating token: ", e);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = getSigningKey();
            return JWT.require(algorithm)
                    .withIssuer("menegati-b2b")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            return "";
        }
    }

    public boolean shouldRenew(String token) {
        Duration jwtDuration = SessionDuration.fromRememberMe(getRememberMe(token)).getJwtExpiration();
        long renewalThreshold = jwtDuration.toMillis() / 2;

        return getExpirationTimeLeft(token) < renewalThreshold;

    }

    public String renewToken(String token){
        try{
            DecodedJWT jwt = JWT.decode(token);

            String cpf = jwt.getSubject();
            String name = jwt.getClaim("name").asString();
            String role = jwt.getClaim("role").asString();
            Boolean rememberMe = jwt.getClaim("rememberMe").asBoolean();

            Instant expirationDate = calcExpirationDate(SessionDuration.fromRememberMe(rememberMe).getJwtExpiration());

            try{
                return JWT.create()
                        .withIssuer("menegati-b2b")
                        .withSubject(cpf)
                        .withClaim("name", name)
                        .withClaim("role", role)
                        .withClaim("rememberMe", rememberMe)
                        .withExpiresAt(expirationDate)
                        .sign(getSigningKey());
            } catch (JWTCreationException e){
                throw new RuntimeException("error generating token: ", e);
            }
        } catch (JWTVerificationException e){
            return "";
        }

    }

    private Instant calcExpirationDate(Duration jwtExpirationTime){
        return Instant.now().plus(jwtExpirationTime);
    }

    private long getExpirationTimeLeft(String token){
        try{
            Date expiration = JWT.decode(token).getExpiresAt();
            return ChronoUnit.MILLIS.between(Instant.now(), expiration.toInstant());
        } catch (Exception e){
            return 0;
        }
    }

    public boolean getRememberMe(String token){
        return JWT.decode(token).getClaim("rememberMe").asBoolean();
    }

    public Instant getExpirationDate(String token){
        return JWT.decode(token).getExpiresAt().toInstant();
    }

    public ResponseCookie buildCookie(String token) {
        int expirationTimeInSeconds = !token.isEmpty()
                ? SessionDuration.fromRememberMe(getRememberMe(token)).getCookieMaxAgeSeconds()
                : 0;

        return ResponseCookie.from("token", token)
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(expirationTimeInSeconds)
                .build();
    }

}
