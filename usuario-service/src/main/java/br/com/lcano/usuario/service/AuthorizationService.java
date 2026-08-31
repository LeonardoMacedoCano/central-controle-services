package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Usuario;
import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthorizationService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Value("${auth.allowed-emails:}")
    private String allowedEmails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario findUsuarioByUsername(String username) {
        return usuarioRepository.findUsuarioByUsername(username);
    }

    public LoginResponseDTO loginWithGoogle(String credential) {
        GoogleIdToken.Payload payload = verificarCredential(credential);

        String email = payload.getEmail();
        Boolean emailVerificado = payload.getEmailVerified();
        if (email == null || !Boolean.TRUE.equals(emailVerificado)) {
            throw new UsuarioException.GoogleTokenInvalido();
        }

        String emailNormalizado = email.toLowerCase(Locale.ROOT);
        if (!emailAutorizado(emailNormalizado)) {
            throw new UsuarioException.EmailNaoAutorizado();
        }

        Usuario usuario = usuarioRepository.findByGoogleSub(payload.getSubject())
                .orElseGet(() -> criarUsuario(payload.getSubject(), emailNormalizado));

        if (!usuario.isEnabled()) {
            throw new UsuarioException.UsuarioDesativado();
        }

        String token = tokenService.generateToken(usuario);
        return mapToLoginResponseDTO(usuario, token);
    }

    public LoginResponseDTO validateToken(String token) {
        Long idUser = tokenService.validateToken(token);
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow(UsuarioException.UsuarioNaoEncontrado::new);
        return mapToLoginResponseDTO(usuario, token);
    }

    private GoogleIdToken.Payload verificarCredential(String credential) {
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(credential);
            if (idToken == null) {
                throw new UsuarioException.GoogleTokenInvalido();
            }
            return idToken.getPayload();
        } catch (UsuarioException e) {
            throw e;
        } catch (Exception e) {
            throw new UsuarioException.GoogleTokenInvalido();
        }
    }

    private boolean emailAutorizado(String email) {
        Set<String> permitidos = Arrays.stream(allowedEmails.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        return permitidos.contains(email);
    }

    private Usuario criarUsuario(String googleSub, String email) {
        return usuarioRepository.save(new Usuario(googleSub, email, new Date()));
    }

    private LoginResponseDTO mapToLoginResponseDTO(Usuario usuario, String token) {
        String iconeBase64 = null;

        if (usuario.getIcone() != null) {
            iconeBase64 = Base64.getEncoder().encodeToString(usuario.getIcone());
        }

        return new LoginResponseDTO(
                usuario.getUsername(),
                token,
                usuario.getTema() != null ? usuario.getTema().getId() : null,
                iconeBase64
        );
    }
}
