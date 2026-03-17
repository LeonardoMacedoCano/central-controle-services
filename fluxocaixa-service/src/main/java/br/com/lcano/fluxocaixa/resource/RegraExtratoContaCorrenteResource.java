package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.RegraExtratoContaCorrenteDTO;
import br.com.lcano.fluxocaixa.service.RegraExtratoContaCorrenteService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/regra-extrato-conta-corrente")
@AllArgsConstructor
public class RegraExtratoContaCorrenteResource {

    private final RegraExtratoContaCorrenteService service;

    @GetMapping("/{id}")
    public RegraExtratoContaCorrenteDTO findById(@PathVariable Long id) {
        return service.findByIdAsDto(id);
    }

    @GetMapping("/search")
    public Page<RegraExtratoContaCorrenteDTO> search(
            @RequestParam(required = false) String filter,
            Pageable pageable
    ) {
        return service.search(filter, pageable);
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody RegraExtratoContaCorrenteDTO dto) {
        Long id = service.save(dto);
        return Map.of("success", "Regra salva com sucesso.", "id", id);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        return Map.of("success", "Regra deletada com sucesso.");
    }
}
