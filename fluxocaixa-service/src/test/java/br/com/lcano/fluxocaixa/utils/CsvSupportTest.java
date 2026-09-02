package br.com.lcano.fluxocaixa.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvSupportTest {

    @Test
    void detectaVirgulaComoPadrao() {
        assertThat(CsvSupport.detectarDelimitador("data,valor,saldo,descricao")).isEqualTo(',');
        assertThat(CsvSupport.detectarDelimitador(null)).isEqualTo(',');
    }

    @Test
    void usaPontoEVirgulaApenasQuandoNaoHaVirgula() {
        assertThat(CsvSupport.detectarDelimitador("data;valor;saldo;descricao")).isEqualTo(';');
        assertThat(CsvSupport.detectarDelimitador("data;valor,com virgula;descricao")).isEqualTo(',');
    }

    @Test
    void splitSimples() {
        assertThat(CsvSupport.split("a,b,c", ',')).containsExactly("a", "b", "c");
    }

    @Test
    void splitRespeitaCamposEntreAspas() {
        assertThat(CsvSupport.split("01/01/2026,\"MERCADO ABC, LTDA\",100,50", ','))
                .containsExactly("01/01/2026", "MERCADO ABC, LTDA", "100", "50");
    }

    @Test
    void splitTrataAspasEscapada() {
        assertThat(CsvSupport.split("\"diz \"\"ola\"\"\",x", ','))
                .containsExactly("diz \"ola\"", "x");
    }

    @Test
    void splitColunaVaziaNoFim() {
        assertThat(CsvSupport.split("a,b,", ',')).containsExactly("a", "b", "");
    }
}
