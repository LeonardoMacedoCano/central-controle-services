package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.MovimentacaoCategoriaDTO;
import br.com.lcano.fluxocaixa.service.MovimentacaoCategoriaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/movimentacao-categoria")
public class MovimentacaoCategoriaResource {

    @Autowired
    private final MovimentacaoCategoriaService service;

    @GetMapping("/search")
    public ResponseEntity<Page<MovimentacaoCategoriaDTO>> search(
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable
    ) {
        Page<MovimentacaoCategoriaDTO> page = service.findAllAsDto(filter, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<Object> saveAsDto(@RequestBody MovimentacaoCategoriaDTO dto) {
        Long id = service.saveAsDto(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Categoria salva com sucesso.", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Categoria deletada com sucesso."));
    }
}
