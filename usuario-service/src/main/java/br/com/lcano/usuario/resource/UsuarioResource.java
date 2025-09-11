package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.UsuarioFormDTO;
import br.com.lcano.usuario.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/usuario")
public class UsuarioResource {

    private final UsuarioService service;

    @PutMapping
    public ResponseEntity<Map<String, String>> updateAsDto(@ModelAttribute UsuarioFormDTO dto) throws Exception {
        service.updateAsDto(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Usuário salvo com sucesso."));
    }
}
