package br.com.lcano.fluxocaixa.enums;

import lombok.Getter;

@Getter
public enum TipoRegraExtratoContaCorrente {
    IGNORAR_DESPESA("IGNORAR_DESPESA"),
    CLASSIFICAR_DESPESA("CLASSIFICAR_DESPESA"),
    IGNORAR_RENDA("IGNORAR_RENDA"),
    CLASSIFICAR_RENDA("CLASSIFICAR_RENDA"),
    CLASSIFICAR_ATIVO("CLASSIFICAR_ATIVO");

    private final String descricao;

    TipoRegraExtratoContaCorrente(String descricao) {
        this.descricao = descricao;
    }

    public boolean isDespesa() {
        return this == CLASSIFICAR_DESPESA || this == IGNORAR_DESPESA;
    }

    public boolean isRenda() {
        return this == CLASSIFICAR_RENDA || this == IGNORAR_RENDA;
    }

    public boolean isAtivo() {
        return this == CLASSIFICAR_ATIVO;
    }
}