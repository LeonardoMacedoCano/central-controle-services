package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.GoogleLoginRequestDTO;
import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {

    private final AuthorizationService service;

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> loginWithGoogle(@RequestBody GoogleLoginRequestDTO request) {
        LoginResponseDTO response = service.loginWithGoogle(request.getCredential());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<LoginResponseDTO> validateToken(@RequestParam String token) {
        LoginResponseDTO response = service.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
