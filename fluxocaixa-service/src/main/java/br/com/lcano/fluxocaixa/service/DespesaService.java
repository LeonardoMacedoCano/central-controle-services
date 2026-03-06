package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Despesa;
import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.dto.DespesaDTO;
import br.com.lcano.fluxocaixa.dto.LancamentoItemDTO;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import br.com.lcano.fluxocaixa.repository.DespesaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DespesaService implements LancamentoItemService {

    private final DespesaRepository repository;

    @Override
    @Transactional
    public void save(LancamentoItemDTO dto, Lancamento lancamento) {
        if (!(dto instanceof DespesaDTO despesaDTO)) {
            throw new IllegalArgumentException("DTO inválido para DESPESA");
        }

        Despesa entity = despesaDTO.toEntity();
        entity.setLancamento(lancamento);

        repository.save(entity);
    }

    @Override
    public LancamentoItemDTO findByLancamentoId(Long id) {
        Despesa entity = repository.findByLancamentoId(id)
                .orElseThrow(() ->
                        new RuntimeException("Despesa não encontrada"));

        return (LancamentoItemDTO) new DespesaDTO().fromEntity(entity);
    }

    @Override
    public TipoLancamento getTipo() {
        return TipoLancamento.DESPESA;
    }
}
