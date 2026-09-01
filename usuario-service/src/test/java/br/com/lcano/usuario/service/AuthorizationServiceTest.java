package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    private UsuarioRepository usuarioRepository;
    private TokenService tokenService;
    private GoogleIdTokenVerifier verifier;
    private AuthorizationService service;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        tokenService = mock(TokenService.class);
        verifier = mock(GoogleIdTokenVerifier.class);
        service = new AuthorizationService(usuarioRepository, tokenService, verifier);
        ReflectionTestUtils.setField(service, "allowedEmails", " Foo@Bar.com , outro@dominio.com ");
        when(tokenService.generateToken(any())).thenReturn("jwt-abc");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private void stubVerifier(String email, Boolean emailVerified, String sub) throws Exception {
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail(email);
        payload.setEmailVerified(emailVerified);
        payload.setSubject(sub);
        when(idToken.getPayload()).thenReturn(payload);
        when(verifier.verify(anyString())).thenReturn(idToken);
    }

    @Test
    void emailNaoVerificado_lancaGoogleTokenInvalido() throws Exception {
        stubVerifier("foo@bar.com", false, "sub-1");
        assertThatThrownBy(() -> service.loginWithGoogle("cred"))
                .isInstanceOf(UsuarioException.GoogleTokenInvalido.class);
    }

    @Test
    void verifierRetornaNull_lancaGoogleTokenInvalido() throws Exception {
        when(verifier.verify(anyString())).thenReturn(null);
        assertThatThrownBy(() -> service.loginWithGoogle("cred"))
                .isInstanceOf(UsuarioException.GoogleTokenInvalido.class);
    }

    @Test
    void verifierLancaExcecao_viraGoogleTokenInvalido() throws Exception {
        when(verifier.verify(anyString())).thenThrow(new RuntimeException("boom"));
        assertThatThrownBy(() -> service.loginWithGoogle("cred"))
                .isInstanceOf(UsuarioException.GoogleTokenInvalido.class);
    }

    @Test
    void emailForaDaAllowlist_lancaEmailNaoAutorizado() throws Exception {
        stubVerifier("intruso@evil.com", true, "sub-2");
        assertThatThrownBy(() -> service.loginWithGoogle("cred"))
                .isInstanceOf(UsuarioException.EmailNaoAutorizado.class);
    }

    @Test
    void allowlistEhCaseInsensitiveEIgnoraEspacos_criaUsuarioNovo() throws Exception {
        stubVerifier("FOO@BAR.com", true, "sub-3");
        when(usuarioRepository.findByGoogleSub("sub-3")).thenReturn(Optional.empty());

        LoginResponseDTO dto = service.loginWithGoogle("cred");

        assertThat(dto.getToken()).isEqualTo("jwt-abc");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void usuarioExistenteDesativado_lancaUsuarioDesativado() throws Exception {
        stubVerifier("foo@bar.com", true, "sub-4");
        Usuario existente = new Usuario("sub-4", "foo@bar.com", new Date());
        existente.setAtivo(false);
        when(usuarioRepository.findByGoogleSub("sub-4")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.loginWithGoogle("cred"))
                .isInstanceOf(UsuarioException.UsuarioDesativado.class);
    }

    @Test
    void usuarioExistenteAtivo_retornaTokenSemCriarNovo() throws Exception {
        stubVerifier("foo@bar.com", true, "sub-5");
        Usuario existente = new Usuario("sub-5", "foo@bar.com", new Date());
        when(usuarioRepository.findByGoogleSub("sub-5")).thenReturn(Optional.of(existente));

        LoginResponseDTO dto = service.loginWithGoogle("cred");

        assertThat(dto.getToken()).isEqualTo("jwt-abc");
        verify(usuarioRepository, never()).save(any());
    }
}
