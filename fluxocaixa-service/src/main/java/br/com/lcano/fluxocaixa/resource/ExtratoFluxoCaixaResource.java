package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.ImportacaoExtratoDTO;
import br.com.lcano.fluxocaixa.service.ExtratoFluxoCaixaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping("/api/extrato-fluxo-caixa")
@RequiredArgsConstructor
public class ExtratoFluxoCaixaResource {

    private final ExtratoFluxoCaixaService service;

    @PostMapping("/import-extrato-conta-corrente")
    public ResponseEntity<ImportacaoExtratoDTO> importarContaCorrente(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(service.importarContaCorrente(file));
    }

    @PostMapping("/import-extrato-fatura-cartao")
    public ResponseEntity<ImportacaoExtratoDTO> importarFaturaCartao(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataVencimento) {
        return ResponseEntity.ok(service.importarFaturaCartao(file, dataVencimento));
    }

    @PostMapping("/import-extrato-movimentacao-b3")
    public ResponseEntity<ImportacaoExtratoDTO> importarMovimentacaoB3(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(service.importarMovimentacaoB3(file));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ImportacaoExtratoDTO> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarStatus(id));
    }
}
