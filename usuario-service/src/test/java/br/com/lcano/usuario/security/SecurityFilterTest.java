package br.com.lcano.usuario.security;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.repository.UsuarioRepository;
import br.com.lcano.usuario.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private TokenService tokenService;
    private UsuarioRepository usuarioRepository;
    private SecurityFilter filter;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        usuarioRepository = mock(UsuarioRepository.class);
        filter = new SecurityFilter(tokenService, usuarioRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest req(String bearer) {
        MockHttpServletRequest r = new MockHttpServletRequest();
        if (bearer != null) r.addHeader("Authorization", "Bearer " + bearer);
        return r;
    }

    @Test
    void tokenValido_autenticaEProssegue() throws Exception {
        Usuario u = new Usuario();
        u.setId(9L);
        u.setUsername("nine");
        when(tokenService.validateToken("tok")).thenReturn(9L);
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(u));

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(req("tok"), res, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(u);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void tokenInvalido_retorna401JsonENaoProssegue() throws Exception {
        when(tokenService.validateToken("ruim")).thenThrow(new UsuarioException.TokenExpiradoOuInvalido());

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(req("ruim"), res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("error");
        assertThat(chain.getRequest()).isNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void usuarioDoTokenNaoExiste_retorna401() throws Exception {
        when(tokenService.validateToken("tok")).thenReturn(123L);
        when(usuarioRepository.findById(123L)).thenReturn(Optional.empty());

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(req("tok"), res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void semHeader_prossegueSemAutenticar() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(req(null), res, chain);

        assertThat(chain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(tokenService);
    }
}
