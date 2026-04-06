package br.com.lcano.fluxocaixa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoriaResumoDTO {
    private String categoria;
    private BigDecimal total;
}
