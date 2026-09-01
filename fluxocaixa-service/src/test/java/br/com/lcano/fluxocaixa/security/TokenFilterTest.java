package br.com.lcano.fluxocaixa.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class TokenFilterTest {

    private static final String SECRET = "segredo-de-teste-1234567890";
    private static final String ISSUER = "usuario-service";

    private final TokenFilter filter = new TokenFilter(SECRET, ISSUER);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private String token(String secret, String issuer, Instant exp) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject("42")
                .withExpiresAt(Date.from(exp))
                .sign(Algorithm.HMAC256(secret));
    }

    private MockHttpServletRequest requestComToken(String token) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        if (token != null) {
            req.addHeader("Authorization", "Bearer " + token);
        }
        return req;
    }

    @Test
    void tokenValido_populaSecurityContextEProssegue() throws Exception {
        MockHttpServletRequest req = requestComToken(token(SECRET, ISSUER, Instant.now().plusSeconds(60)));
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("42");
        assertThat(chain.getRequest()).isNotNull();
        assertThat(res.getStatus()).isEqualTo(200);
    }

    @Test
    void assinaturaInvalida_retorna401JsonENaoProssegue() throws Exception {
        MockHttpServletRequest req = requestComToken(token("outro-segredo-aaaaaaaaaaa", ISSUER, Instant.now().plusSeconds(60)));
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("error");
        assertThat(chain.getRequest()).isNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void tokenExpirado_retorna401() throws Exception {
        MockHttpServletRequest req = requestComToken(token(SECRET, ISSUER, Instant.now().minusSeconds(10)));
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void issuerDiferente_retorna401() throws Exception {
        MockHttpServletRequest req = requestComToken(token(SECRET, "outro-issuer", Instant.now().plusSeconds(60)));
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    void semHeaderAuthorization_prossegueSemAutenticar() throws Exception {
        MockHttpServletRequest req = requestComToken(null);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertThat(chain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
