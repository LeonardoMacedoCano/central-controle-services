package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.exception.UsuarioException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String tokenSecret;

    @Value("${spring.jackson.time-zone}")
    private String timeZone;

    @Value("${api.security.token.expiration-hours}")
    private int tokenExpirationHours;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
            return JWT.create()
                    .withIssuer("usuario-service")
                    .withSubject(usuario.getId().toString())
                    .withExpiresAt(getDataExpiracao())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new UsuarioException.ErroGerarToken();
        }
    }

    public Long validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
            String subject = JWT.require(algorithm)
                    .withIssuer("usuario-service")
                    .build()
                    .verify(token)
                    .getSubject();

            try {
                return Long.valueOf(subject);
            } catch (NumberFormatException e) {
                throw new UsuarioException.TokenExpiradoOuInvalido();
            }

        } catch (JWTVerificationException e) {
            throw new UsuarioException.TokenExpiradoOuInvalido();
        }
    }

    private Instant getDataExpiracao() {
        ZoneId zoneId = ZoneId.of(timeZone);
        return LocalDateTime.now(zoneId)
                .plusHours(tokenExpirationHours)
                .toInstant(zoneId.getRules().getOffset(Instant.now()));
    }
}
