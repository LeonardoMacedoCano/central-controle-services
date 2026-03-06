package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Ativo;
import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.dto.AtivoDTO;
import br.com.lcano.fluxocaixa.dto.LancamentoItemDTO;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;
import br.com.lcano.fluxocaixa.repository.AtivoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtivoService implements LancamentoItemService {
    private final AtivoRepository repository;

    @Override
    @Transactional
    public void save(LancamentoItemDTO dto, Lancamento lancamento) {
        if (!(dto instanceof AtivoDTO ativoDTO)) {
            throw new IllegalArgumentException("DTO inválido para ATIVO");
        }

        Ativo entity = ativoDTO.toEntity();
        entity.setLancamento(lancamento);

        repository.save(entity);
    }

    @Override
    public LancamentoItemDTO findByLancamentoId(Long id) {
        Ativo entity = repository.findByLancamentoId(id)
                .orElseThrow(() ->
                        new RuntimeException("Ativo não encontrado"));

        return (LancamentoItemDTO) new AtivoDTO().fromEntity(entity);
    }

    @Override
    public TipoLancamento getTipo() {
        return TipoLancamento.ATIVO;
    }
}
