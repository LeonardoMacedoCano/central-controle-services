package br.com.lcano.fluxocaixa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
public class ExtratoMovimentacaoB3DTO {
    private String tipoOperacao;
    private Date dataMovimentacao;
    private String tipoMovimentacao;
    private String produto;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal precoTotal;
}
