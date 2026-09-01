package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import br.com.lcano.fluxocaixa.dto.MovimentacaoCategoriaDTO;
import br.com.lcano.fluxocaixa.exception.MovimentacaoCategoriaException;
import br.com.lcano.fluxocaixa.repository.MovimentacaoCategoriaRepository;
import br.com.lcano.fluxocaixa.rsql.RsqlSpecUtil;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MovimentacaoCategoriaService {

    private final MovimentacaoCategoriaRepository repository;

    public Page<MovimentacaoCategoria> findAll(Specification<MovimentacaoCategoria> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    public Page<MovimentacaoCategoriaDTO> findAllAsDto(String filter, Pageable pageable) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        Specification<MovimentacaoCategoria> spec = visivelPara(idUsuario);

        Specification<MovimentacaoCategoria> rsql = RsqlSpecUtil.fromFilter(filter);
        if (rsql != null) {
            spec = spec.and(rsql);
        }

        return this.findAll(spec, pageable)
                .map(entity -> new MovimentacaoCategoriaDTO().fromEntity(entity));
    }

    public Long saveAsDto(MovimentacaoCategoriaDTO dto) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        MovimentacaoCategoria categoria = dto.toEntity();

        if (categoria.getId() != null) {
            MovimentacaoCategoria existente = repository.findById(categoria.getId())
                    .filter(c -> idUsuario.equals(c.getIdUsuario()))
                    .orElseThrow(() -> new MovimentacaoCategoriaException.CategoriaNaoEncontrada(categoria.getId()));
            existente.setDescricao(categoria.getDescricao());
            existente.setTipo(categoria.getTipo());
            return repository.save(existente).getId();
        }

        categoria.setIdUsuario(idUsuario);
        return repository.save(categoria).getId();
    }

    public void deleteById(Long id) {
        Long idUsuario = UsuarioUtil.getIdUsuarioAutenticado();
        MovimentacaoCategoria categoria = repository.findById(id)
                .filter(c -> idUsuario.equals(c.getIdUsuario()))
                .orElseThrow(() -> new MovimentacaoCategoriaException.CategoriaNaoEncontrada(id));
        repository.delete(categoria);
    }

    private Specification<MovimentacaoCategoria> visivelPara(Long idUsuario) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("idUsuario"), idUsuario),
                cb.isNull(root.get("idUsuario"))
        );
    }
}
