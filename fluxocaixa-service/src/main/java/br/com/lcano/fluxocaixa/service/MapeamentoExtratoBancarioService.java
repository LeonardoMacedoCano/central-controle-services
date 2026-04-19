package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.MapeamentoExtratoBancario;
import br.com.lcano.fluxocaixa.dto.MapeamentoExtratoBancarioDTO;
import br.com.lcano.fluxocaixa.exception.MapeamentoExtratoBancarioException;
import br.com.lcano.fluxocaixa.repository.MapeamentoExtratoBancarioRepository;
import br.com.lcano.fluxocaixa.rsql.RsqlSpecUtil;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MapeamentoExtratoBancarioService {
    private final MapeamentoExtratoBancarioRepository repository;

    public MapeamentoExtratoBancarioDTO findByIdAsDto(Long id) {
        return repository.findById(id)
                .map(entity -> new MapeamentoExtratoBancarioDTO().fromEntity(entity))
                .orElseThrow(() -> new MapeamentoExtratoBancarioException.MapeamentoNaoEncontrado(id));
    }

    public Page<MapeamentoExtratoBancarioDTO> search(String filter, Pageable pageable) {
        Specification<MapeamentoExtratoBancario> spec = RsqlSpecUtil.fromFilter(buildUserFilter(filter));
        return repository.findAll(spec, pageable)
                .map(entity -> new MapeamentoExtratoBancarioDTO().fromEntity(entity));
    }

    public Long save(MapeamentoExtratoBancarioDTO dto) {
        MapeamentoExtratoBancario mapeamento = dto.toEntity();
        mapeamento.setIdUsuario(UsuarioUtil.getIdUsuarioAutenticado());
        MapeamentoExtratoBancario saved = repository.save(mapeamento);
        return saved.getId();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private String buildUserFilter(String filter) {
        String userFilter = "idUsuario==" + UsuarioUtil.getIdUsuarioAutenticado();
        return (filter == null || filter.isBlank())
                ? userFilter
                : userFilter + ";" + filter;
    }
}
