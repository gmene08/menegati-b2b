package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${JWT_SECRET}")
    private String jwtSecretString;

    private Algorithm getSigningKey(){
        return Algorithm.HMAC256(jwtSecretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user){
        try{
            return JWT.create()
                    .withIssuer("menegati-b2b")
                    .withSubject(user.getCpf())
                    .withClaim("name", user.getName())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(getExpirationDate())
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

    private Instant getExpirationDate(){
        int jwtExpirationTime = 8;
        return LocalDateTime.now().plusHours(jwtExpirationTime).toInstant(ZoneOffset.UTC);
    }
}
