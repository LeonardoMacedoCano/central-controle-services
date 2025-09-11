package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.config.PropertiesConfig;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class TokenService {

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(PropertiesConfig.getTokenSecret());
            return JWT.create()
                    .withIssuer("usuario-service")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(getDataExpiracao())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new UsuarioException.ErroGerarToken();
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(PropertiesConfig.getTokenSecret());
            return JWT.require(algorithm)
                    .withIssuer("usuario-service")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new UsuarioException.TokenExpiradoOuInvalido();
        }
    }

    private Instant getDataExpiracao() {
        ZoneId zoneId = ZoneId.of(PropertiesConfig.getTimeZone());
        return LocalDateTime.now(zoneId)
                .plusHours(PropertiesConfig.getTokenExpirationHours())
                .toInstant(zoneId.getRules().getOffset(Instant.now()));
    }
}
