package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.DashboardDTO;
import br.com.lcano.fluxocaixa.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardResource {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> findResumo(
            @RequestParam int ano,
            @RequestParam(required = false) Integer mes) {
        return ResponseEntity.ok(dashboardService.findResumo(ano, mes));
    }
}
