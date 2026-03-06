package br.com.lcano.fluxocaixa.service;

import br.com.lcano.fluxocaixa.domain.Lancamento;
import br.com.lcano.fluxocaixa.dto.LancamentoItemDTO;
import br.com.lcano.fluxocaixa.enums.TipoLancamento;

public interface LancamentoItemService {
    void save(LancamentoItemDTO itemDTO, Lancamento lancamento);
    LancamentoItemDTO findByLancamentoId(Long lancamentoId);
    TipoLancamento getTipo();
}
