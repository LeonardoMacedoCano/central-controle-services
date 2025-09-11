package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.LoginRequestDTO;
import br.com.lcano.usuario.dto.LoginResponseDTO;
import br.com.lcano.usuario.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO usuarioDTO) {
        LoginResponseDTO response = service.login(usuarioDTO, authenticationManager);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody LoginRequestDTO data) {
        service.register(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validateToken")
    public ResponseEntity<LoginResponseDTO> validateToken(@RequestParam String token) {
        LoginResponseDTO response = service.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
