package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Renda;
import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.dto.RendaDTO;
import br.com.lcano.fluxocaixa.dto.LancamentoItemDTO;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import br.com.lcano.fluxocaixa.repository.RendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RendaService implements LancamentoItemService {

    private final RendaRepository repository;

    @Override
    @Transactional
    public void save(LancamentoItemDTO dto, Lancamento lancamento) {
        if (!(dto instanceof RendaDTO rendaDTO)) {
            throw new IllegalArgumentException("DTO inválido para RENDA");
        }

        Renda entity = rendaDTO.toEntity();
        entity.setLancamento(lancamento);

        repository.save(entity);
    }

    @Override
    public LancamentoItemDTO findByLancamentoId(Long id) {
        Renda entity = repository.findByLancamentoId(id)
                .orElseThrow(() ->
                        new RuntimeException("Renda não encontrada"));

        return (LancamentoItemDTO) new RendaDTO().fromEntity(entity);
    }

    @Override
    public TipoLancamento getTipo() {
        return TipoLancamento.RENDA;
    }
}

