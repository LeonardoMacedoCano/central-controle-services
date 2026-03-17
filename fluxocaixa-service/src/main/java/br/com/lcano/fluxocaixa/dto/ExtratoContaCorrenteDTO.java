package br.com.lcano.fluxocaixa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class ExtratoContaCorrenteDTO {
    private Date dataLancamento;
    private BigDecimal valor;
    private String descricao;
}
