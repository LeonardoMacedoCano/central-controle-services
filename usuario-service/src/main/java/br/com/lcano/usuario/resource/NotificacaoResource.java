package br.com.lcano.usuario.resource;

import br.com.lcano.usuario.dto.NotificacaoDTO;
import br.com.lcano.usuario.dto.NotificacaoInternaDTO;
import br.com.lcano.usuario.service.NotificacaoService;
import br.com.lcano.usuario.util.UsuarioUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificacao")
@RequiredArgsConstructor
public class NotificacaoResource {

    private final NotificacaoService service;
    private final UsuarioUtil usuarioUtil;

    @PostMapping("/interna")
    public ResponseEntity<Void> receberInterna(
            @RequestHeader("X-Service-Secret") String secret,
            @RequestBody NotificacaoInternaDTO dto) {
        service.receberInterna(secret, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<NotificacaoDTO>> findByUsuario(
            @RequestParam(defaultValue = "false") boolean apenasNaoLidas,
            Pageable pageable) {
        Long idUsuario = usuarioUtil.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(service.findByUsuario(idUsuario, apenasNaoLidas, pageable));
    }

    @GetMapping("/nao-lidas/count")
    public ResponseEntity<Map<String, Long>> countNaoLidas() {
        Long idUsuario = usuarioUtil.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(Map.of("total", service.countNaoLidas(idUsuario)));
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        Long idUsuario = usuarioUtil.getUsuarioAutenticado().getId();
        service.marcarComoLida(id, idUsuario);
        return ResponseEntity.ok().build();
    }
}
