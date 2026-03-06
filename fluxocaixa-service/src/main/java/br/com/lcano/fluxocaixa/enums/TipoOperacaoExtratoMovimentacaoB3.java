package br.com.lcano.fluxocaixa.enums;

import lombok.Getter;
import java.util.Map;
import java.util.HashMap;

@Getter
public enum TipoOperacaoExtratoMovimentacaoB3 {
    DEBITO("DEBITO"),
    CREDITO("CREDITO");

    private final String descricaoParaAtivo;

    private static final Map<String, TipoOperacaoExtratoMovimentacaoB3> descricaoMap = new HashMap<>();

    static {
        for (TipoOperacaoExtratoMovimentacaoB3 operacao : values()) {
            descricaoMap.put(operacao.getDescricaoParaAtivo().toUpperCase(), operacao);
        }
    }

    TipoOperacaoExtratoMovimentacaoB3(String descricao) {
        this.descricaoParaAtivo = descricao;
    }

    public static TipoOperacaoExtratoMovimentacaoB3 fromDescricao(String descricao) {
        TipoOperacaoExtratoMovimentacaoB3 operacao = descricaoMap.get(descricao.toUpperCase());
        if (operacao == null) {
            throw new IllegalArgumentException("Tipo de movimentação inválido: " + descricao);
        }
        return operacao;
    }
}