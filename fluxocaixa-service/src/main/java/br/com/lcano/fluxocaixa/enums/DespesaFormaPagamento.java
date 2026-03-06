package br.com.lcano.fluxocaixa.enums;

import lombok.Getter;

@Getter
public enum DespesaFormaPagamento {
    DINHEIRO("Dinheiro"),
    PIX("PIX"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito");

    private final String descricao;

    DespesaFormaPagamento(String descricao) {
        this.descricao = descricao;
    }
}