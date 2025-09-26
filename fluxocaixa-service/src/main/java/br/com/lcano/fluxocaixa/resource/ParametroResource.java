package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.ParametroDTO;
import br.com.lcano.fluxocaixa.service.ParametroService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/parametro")
public class ParametroResource {

    @Autowired
    private final ParametroService service;

    @GetMapping
    public ResponseEntity<ParametroDTO> findByUsuarioAsDto() {
        return ResponseEntity.ok(
                new ParametroDTO().fromEntity(
                        this.service.findByUsuario()
                )
        );
    }

    @PostMapping
    public ResponseEntity<Object> saveAsDto(@RequestBody ParametroDTO parametroDTO) {
        Long id = this.service.saveAsDto(parametroDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Parâmetros salvos com sucesso.", "id", id));
    }
}
