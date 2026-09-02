package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.exception.UsuarioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private static final String SECRET = "segredo-de-teste-abcdefghij";

    private TokenService tokenService;

    private TokenService novoService(String secret, int expirationHours) {
        TokenService s = new TokenService();
        ReflectionTestUtils.setField(s, "tokenSecret", secret);
        ReflectionTestUtils.setField(s, "timeZone", "GMT");
        ReflectionTestUtils.setField(s, "tokenExpirationHours", expirationHours);
        return s;
    }

    private Usuario usuario(long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setUsername("user" + id);
        return u;
    }

    @BeforeEach
    void setUp() {
        tokenService = novoService(SECRET, 24);
    }

    @Test
    void gerarEValidar_roundtripRetornaIdDoUsuario() {
        String token = tokenService.generateToken(usuario(5L));
        assertThat(tokenService.validateToken(token)).isEqualTo(5L);
    }

    @Test
    void validar_comSecretDiferente_lancaTokenInvalido() {
        String token = tokenService.generateToken(usuario(5L));
        TokenService outro = novoService("outro-segredo-totalmente-diferente", 24);

        assertThatThrownBy(() -> outro.validateToken(token))
                .isInstanceOf(UsuarioException.TokenExpiradoOuInvalido.class);
    }

    @Test
    void validar_tokenAdulterado_lancaTokenInvalido() {
        String token = tokenService.generateToken(usuario(5L));
        String adulterado = token.substring(0, token.length() - 3) + "abc";

        assertThatThrownBy(() -> tokenService.validateToken(adulterado))
                .isInstanceOf(UsuarioException.TokenExpiradoOuInvalido.class);
    }

    @Test
    void validar_tokenExpirado_lancaTokenInvalido() {
        TokenService expirado = novoService(SECRET, -1);
        String token = expirado.generateToken(usuario(5L));

        assertThatThrownBy(() -> tokenService.validateToken(token))
                .isInstanceOf(UsuarioException.TokenExpiradoOuInvalido.class);
    }

    @Test
    void validar_lixo_lancaTokenInvalido() {
        assertThatThrownBy(() -> tokenService.validateToken("nao-e-um-jwt"))
                .isInstanceOf(UsuarioException.TokenExpiradoOuInvalido.class);
    }
}
