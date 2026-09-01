package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.GoogleLoginRequestDTO;
import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.exception.UsuarioException;
import br.com.lcano.usuario.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthorizationService service;

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> loginWithGoogle(@RequestBody GoogleLoginRequestDTO request) {
        LoginResponseDTO response = service.loginWithGoogle(request.getCredential());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<LoginResponseDTO> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        LoginResponseDTO response = service.validateToken(extrairToken(authorization));
        return ResponseEntity.ok(response);
    }

    private String extrairToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new UsuarioException.TokenExpiradoOuInvalido();
        }
        return authorization.substring(BEARER_PREFIX.length()).trim();
    }
}
