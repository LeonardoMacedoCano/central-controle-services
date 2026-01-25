package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.RegraExtratoContaCorrente;
import br.com.lcano.fluxocaixa.dto.RegraExtratoContaCorrenteDTO;
import br.com.lcano.fluxocaixa.exception.RegraExtratoContaCorrenteException;
import br.com.lcano.fluxocaixa.repository.RegraExtratoContaCorrenteRepository;
import br.com.lcano.fluxocaixa.rsql.RsqlSpecUtil;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RegraExtratoContaCorrenteService {
    @Autowired
    private final RegraExtratoContaCorrenteRepository repository;

    public RegraExtratoContaCorrenteDTO findDtoById(Long id) {
        return repository.findById(id)
                .map(entity -> new RegraExtratoContaCorrenteDTO().fromEntity(entity))
                .orElseThrow(() -> new RegraExtratoContaCorrenteException.RegraNaoEncontrado(id));
    }

    public Page<RegraExtratoContaCorrenteDTO> search(String filter, Pageable pageable) {
        Specification<RegraExtratoContaCorrente> spec = RsqlSpecUtil.fromFilter(buildUserFilter(filter));
        return repository.findAll(spec, pageable)
                .map(entity -> new RegraExtratoContaCorrenteDTO().fromEntity(entity));
    }

    public Long save(RegraExtratoContaCorrenteDTO dto) {
        RegraExtratoContaCorrente regraExtratoContaCorrente = dto.toEntity();
        regraExtratoContaCorrente.setIdUsuario(UsuarioUtil.getIdUsuarioAutenticado());
        RegraExtratoContaCorrente regraSaved = repository.save(regraExtratoContaCorrente);
        return regraSaved.getId();
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
