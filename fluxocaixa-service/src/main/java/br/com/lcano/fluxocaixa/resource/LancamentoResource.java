package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.LancamentoDTO;
import br.com.lcano.fluxocaixa.service.LancamentoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/lancamento")
public class LancamentoResource {
    private final LancamentoService service;

    @PostMapping
    public ResponseEntity<Object> saveAsDto(@RequestBody LancamentoDTO lancamentoDTO) {
        Long id = this.service.saveAsDto(lancamentoDTO).getId();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Lançamento salvo com sucesso.", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        this.service.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Lançamento deletado com sucesso."));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LancamentoDTO>> search(@RequestParam(value = "filter", required = false) String filter,
                                                      Pageable pageable) {
        return ResponseEntity.ok(service.search(pageable, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoDTO> findByIdAsDto(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findByIdAsDto(id));
    }
}