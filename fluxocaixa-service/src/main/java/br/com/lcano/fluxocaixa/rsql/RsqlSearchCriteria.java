package br.com.lcano.fluxocaixa.rsql;

import lombok.Getter;

import java.util.List;

@Getter
public class RsqlSearchCriteria {
    private String property;
    private RsqlSearchOperation operation;
    private List<String> values;

    public RsqlSearchCriteria(String property, RsqlSearchOperation operation, List<String> values) {
        this.property = property;
        this.operation = operation;
        this.values = values;
    }

}
