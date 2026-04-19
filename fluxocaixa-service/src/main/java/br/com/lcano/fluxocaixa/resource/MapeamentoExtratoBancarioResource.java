package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.MapeamentoExtratoBancarioDTO;
import br.com.lcano.fluxocaixa.service.MapeamentoExtratoBancarioService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mapeamento-extrato-bancario")
@AllArgsConstructor
public class MapeamentoExtratoBancarioResource {

    private final MapeamentoExtratoBancarioService service;

    @GetMapping("/{id}")
    public MapeamentoExtratoBancarioDTO findById(@PathVariable Long id) {
        return service.findByIdAsDto(id);
    }

    @GetMapping("/search")
    public Page<MapeamentoExtratoBancarioDTO> search(
            @RequestParam(required = false) String filter,
            Pageable pageable
    ) {
        return service.search(filter, pageable);
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody MapeamentoExtratoBancarioDTO dto) {
        Long id = service.save(dto);
        return Map.of("success", "Mapeamento salvo com sucesso.", "id", id);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        return Map.of("success", "Mapeamento deletado com sucesso.");
    }
}
