package br.com.lcano.fluxocaixa.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValorParserTest {

    @ParameterizedTest
    @CsvSource({
            "'1234.56', 1234.56",
            "'1234,56', 1234.56",
            "'1.234,56', 1234.56",
            "'-1.234,56', -1234.56",
            "'1.234.567,89', 1234567.89",
            "'R$ 1.234,56', 1234.56",
            "'R$ 1.234,56 ', 1234.56",
            "'1,234.56', 1234.56",
            "'1000', 1000",
            "'-50', -50",
            "'0,00', 0.00",
            "'(1.234,56)', -1234.56"
    })
    void converteFormatosComuns(String entrada, String esperado) {
        assertThat(ValorParser.parse(entrada)).isEqualByComparingTo(new BigDecimal(esperado));
    }

    @Test
    void valorApenasComPontoMantemComportamentoLegado() {
        assertThat(ValorParser.parse("1.50")).isEqualByComparingTo("1.50");
        assertThat(ValorParser.parse("1.234")).isEqualByComparingTo("1.234");
    }

    @Test
    void textoInvalidoLancaNumberFormatException() {
        assertThatThrownBy(() -> ValorParser.parse("abc")).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> ValorParser.parse("")).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> ValorParser.parse("   ")).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> ValorParser.parse(null)).isInstanceOf(NumberFormatException.class);
    }
}
