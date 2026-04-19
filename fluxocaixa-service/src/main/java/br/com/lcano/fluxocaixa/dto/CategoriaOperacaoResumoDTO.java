package br.com.lcano.fluxocaixa.dto;

import br.com.lcano.fluxocaixa.enums.TipoOperacaoExtratoMovimentacaoB3;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoriaOperacaoResumoDTO {
    private String categoria;
    private TipoOperacaoExtratoMovimentacaoB3 operacao;
    private BigDecimal total;
}
