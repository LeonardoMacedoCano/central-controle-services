package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.TemaDTO;
import br.com.lcano.usuario.service.TemaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tema")
public class TemaResource {

    private final TemaService service;

    @GetMapping
    public ResponseEntity<List<TemaDTO>> findAllAsDto() {
        return ResponseEntity.ok(service.findAllAsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemaDTO> findByIdAsDto(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByIdAsDtoOrThrow(id));
    }

    @GetMapping("/default")
    public ResponseEntity<TemaDTO> getDefaultTheme() {
        return ResponseEntity.ok(service.findDefaultThemeAsDtoOrThrow());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveAsDto(@RequestBody TemaDTO dto) {
        Long id = service.saveAsDto(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Tema salvo com sucesso.", "id", id));
    }
}
