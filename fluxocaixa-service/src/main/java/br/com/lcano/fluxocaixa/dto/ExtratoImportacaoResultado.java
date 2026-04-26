package br.com.lcano.fluxocaixa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ExtratoImportacaoResultado {

    private int processadas;
    private int ignoradas;
    private Date dataInicioPeriodo;
    private Date dataFimPeriodo;
}
