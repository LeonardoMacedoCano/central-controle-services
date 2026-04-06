package br.com.lcano.fluxocaixa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class DashboardDTO {
    private BigDecimal totalReceita;
    private BigDecimal totalDespesa;
    private BigDecimal saldo;
    private BigDecimal totalInvestidoAtivos;
    private List<CategoriaResumoDTO> despesasPorCategoria;
    private List<CategoriaResumoDTO> receitasPorCategoria;
    private List<CategoriaResumoDTO> ativosPorCategoria;
}
