package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.MovimentacaoCategoria;
import br.com.lcano.fluxocaixa.dto.MovimentacaoCategoriaDTO;
import br.com.lcano.fluxocaixa.repository.MovimentacaoCategoriaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class MovimentacaoCategoriaService {

    @Autowired
    private final MovimentacaoCategoriaRepository repository;

    public Page<MovimentacaoCategoriaDTO> findAllPagedAsDto(Pageable pageable) {
        return repository.findAll(pageable)
                .map(entity -> new MovimentacaoCategoriaDTO().fromEntity(entity));
    }

    public Long saveAsDto(MovimentacaoCategoriaDTO dto) {
        MovimentacaoCategoria categoria = dto.toEntity();
        MovimentacaoCategoria categoriaSaved = repository.save(categoria);
        return categoriaSaved.getId();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
