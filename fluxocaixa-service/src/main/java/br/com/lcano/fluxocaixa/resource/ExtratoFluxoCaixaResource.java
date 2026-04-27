package br.com.lcano.fluxocaixa.resource;

import br.com.lcano.fluxocaixa.dto.ImportacaoExtratoDTO;
import br.com.lcano.fluxocaixa.dto.LancamentoDTO;
import br.com.lcano.fluxocaixa.service.ExtratoFluxoCaixaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<ImportacaoExtratoDTO> findStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findStatusById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ImportacaoExtratoDTO>> search(
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable) {
        return ResponseEntity.ok(service.search(pageable, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportacaoExtratoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/lancamentos/search")
    public ResponseEntity<Page<LancamentoDTO>> searchLancamentos(
            @PathVariable Long id,
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable) {
        return ResponseEntity.ok(service.searchLancamentos(id, filter, pageable));
    }

    @GetMapping("/{id}/arquivo")
    public ResponseEntity<byte[]> downloadArquivo(@PathVariable Long id) {
        return service.downloadArquivo(id);
    }
}
