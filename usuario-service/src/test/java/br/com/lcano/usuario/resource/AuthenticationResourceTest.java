package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthenticationResourceTest {

    private AuthorizationService service;
    private AuthenticationResource resource;

    @BeforeEach
    void setUp() {
        service = mock(AuthorizationService.class);
        resource = new AuthenticationResource(service);
    }

    @Test
    void validateToken_extraiTokenDoHeaderBearer() {
        LoginResponseDTO dto = new LoginResponseDTO("user", "tok", null, null);
        when(service.validateToken("tok")).thenReturn(dto);

        var response = resource.validateToken("Bearer tok");

        assertThat(response.getBody()).isSameAs(dto);
        verify(service).validateToken("tok");
    }

    @Test
    void validateToken_semHeader_lancaTokenInvalido() {
        assertThatThrownBy(() -> resource.validateToken(null))
                .isInstanceOf(UsuarioException.TokenExpiradoOuInvalido.class);
        verifyNoInteractions(service);
    }

    @Test
    void validateToken_headerSemPrefixoBearer_lancaTokenInvalido() {
        assertThatThrownBy(() -> resource.validateToken("tok"))
                .isInstanceOf(UsuarioException.TokenExpiradoOuInvalido.class);
    }
}
