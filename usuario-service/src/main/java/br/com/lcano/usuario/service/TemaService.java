package br.com.lcano.usuario.service;

import br.com.lcano.usuario.domain.Tema;
import br.com.lcano.usuario.dto.TemaDTO;
import br.com.lcano.usuario.exception.TemaException;
import br.com.lcano.usuario.repository.TemaRepository;
import br.com.lcano.usuario.util.UsuarioUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TemaService {

    private final TemaRepository repository;
    private final UsuarioUtil usuarioUtil;

    public List<TemaDTO> findAllAsDto() {
        Long usuarioId = usuarioUtil.getUsuarioAutenticado().getId();
        return repository.findByUsuarioIdOrUsuarioIsNull(usuarioId).stream()
                .map(entity -> new TemaDTO().fromEntity(entity))
                .toList();
    }

    public TemaDTO findDefaultThemeAsDtoOrThrow() {
        return repository.findByIsDefault(Boolean.TRUE)
                .map(entity -> new TemaDTO().fromEntity(entity))
                .orElseThrow(TemaException.TemaPadraoNaoEncontrado::new);
    }

    public TemaDTO findByIdAsDtoOrThrow(Long id) {
        return repository.findById(id)
                .map(entity -> new TemaDTO().fromEntity(entity))
                .orElseThrow(() -> new TemaException.TemaNaoEncontrado(id));
    }

    public Long saveAsDto(TemaDTO dto) {
        Tema entity = dto.toEntity();
        entity.setUsuario(usuarioUtil.getUsuarioAutenticado());
        return repository.save(entity).getId();
    }

    public Tema findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TemaException.TemaNaoEncontrado(id));
    }
}
