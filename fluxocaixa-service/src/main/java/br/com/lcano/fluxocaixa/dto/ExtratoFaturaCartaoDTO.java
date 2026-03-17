package br.com.lcano.fluxocaixa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class ExtratoFaturaCartaoDTO {
    private Date dataLancamento;
    private String descricao;
    private BigDecimal valor;
    private String categoria;
}
