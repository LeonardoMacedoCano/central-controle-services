package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoContaCorrenteDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExtratoContaCorrenteCSVParserTest {

    private static byte[] csv(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void ignoraCabecalhoEParseiaLinhasValidas() {
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(csv("""
                Data,Valor,Saldo,Descricao
                01/01/2026,100.50,1000.00,Salario
                02/01/2026,-30.00,970.00,Padaria
                """));

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("100.50");
        assertThat(itens.get(0).getDescricao()).isEqualTo("Salario");
        assertThat(itens.get(1).getValor()).isEqualByComparingTo("-30.00");
    }

    @Test
    void aceitaValorBrasileiroComSeparadorDeMilharQuandoEntreAspas() {
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(csv("""
                Data,Valor,Saldo,Descricao
                10/02/2026,"1.234,56",0,Deposito
                """));

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getValor()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    void aceitaDescricaoComVirgulaEntreAspas() {
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(csv("""
                Data,Valor,Saldo,Descricao
                05/01/2026,-45.90,0,"MERCADO ABC, LTDA"
                """));

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getDescricao()).isEqualTo("MERCADO ABC, LTDA");
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("-45.90");
    }

    @Test
    void aceitaArquivoSeparadoPorPontoEVirgula() {
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(csv("""
                Data;Valor;Saldo;Descricao
                07/01/2026;1590,00;0;Deposito
                08/01/2026;-12,30;0;Cafe
                """));

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("1590.00");
        assertThat(itens.get(1).getValor()).isEqualByComparingTo("-12.30");
    }

    @Test
    void descartaLinhasInvalidasSemQuebrarOResto() {
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(csv("""
                Data,Valor,Saldo,Descricao
                so,duas
                03/01/2026,abc,0,Invalida
                32/13/2026,10.00,0,DataInvalida
                04/01/2026,15.00,0,Ok
                """));

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getDescricao()).isEqualTo("Ok");
    }

    @Test
    void parseiaDataNoFormatoDdMmYyyy() {
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(
                csv("Data,Valor,Saldo,Descricao\n15/03/2026,1.00,0,X\n"));

        assertThat(new SimpleDateFormat("yyyy-MM-dd").format(itens.get(0).getDataLancamento()))
                .isEqualTo("2026-03-15");
    }
}
