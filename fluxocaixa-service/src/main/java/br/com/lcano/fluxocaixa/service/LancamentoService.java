package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.dto.LancamentoDTO;
import br.com.lcano.fluxocaixa.dto.LancamentoItemDTO;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import br.com.lcano.fluxocaixa.exception.LancamentoException;
import br.com.lcano.fluxocaixa.repository.LancamentoRepository;
import br.com.lcano.fluxocaixa.rsql.RsqlSpecUtil;
import br.com.lcano.fluxocaixa.utils.UsuarioUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LancamentoService {
    private final LancamentoRepository repository;
    private final List<LancamentoItemService> services;

    private Map<TipoLancamento, LancamentoItemService> serviceMap;

    @PostConstruct
    void init() {
        serviceMap = services.stream()
                .collect(Collectors.toUnmodifiableMap(
                        LancamentoItemService::getTipo,
                        s -> s
                ));
    }

    private LancamentoItemService getService(TipoLancamento tipo) {
        return Optional.ofNullable(serviceMap.get(tipo))
                .orElseThrow(() ->
                        new LancamentoException
                                .LancamentoTipoNaoSuportado(tipo.getDescricao()));
    }

    public LancamentoDTO findByIdAsDto(Long id) {
        return toDto(repository.findById(id)
                .orElseThrow(() ->
                        new LancamentoException.LancamentoNaoEncontradoById(id)));
    }

    @Transactional
    public LancamentoDTO saveAsDto(LancamentoDTO dto) {

        Lancamento entity = dto.toEntity();
        entity.setIdUsuario(UsuarioUtil.getIdUsuarioAutenticado());

        entity = repository.save(entity);

        getService(entity.getTipo())
                .save(dto.getItemDTO(), entity);

        return new LancamentoDTO().fromEntity(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<LancamentoDTO> search(Pageable pageable, String filter) {

        Specification<Lancamento> spec = RsqlSpecUtil.fromFilter(filter);

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dataLancamento").descending()
        );

        return repository.findAll(spec, sorted)
                .map(this::toDto);
    }

    private LancamentoDTO toDto(Lancamento entity) {

        LancamentoItemDTO item =
                getService(entity.getTipo())
                        .findByLancamentoId(entity.getId());

        return new LancamentoDTO()
                .fromEntityWithItem(entity, item);
    }
}
