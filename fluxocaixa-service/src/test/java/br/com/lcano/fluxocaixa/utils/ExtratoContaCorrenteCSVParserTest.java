package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoContaCorrenteDTO;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Caracteriza o comportamento atual do parser de extrato de conta corrente.
 * Ver relatorio de QA: o parser usa split(",") ingenuo e descarta linhas
 * invalidas silenciosamente — comportamento aqui congelado ate haver arquivos
 * de amostra reais para embasar a correcao.
 */
class ExtratoContaCorrenteCSVParserTest {

    private static byte[] csv(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void ignoraCabecalhoEParseiaLinhasValidas() {
        byte[] conteudo = csv("""
                Data,Valor,Saldo,Descricao
                01/01/2026,100.50,1000.00,Salario
                02/01/2026,-30.00,970.00,Padaria
                """);

        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(conteudo);

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("100.50");
        assertThat(itens.get(0).getDescricao()).isEqualTo("Salario");
        assertThat(itens.get(1).getValor()).isEqualByComparingTo("-30.00");
    }

    @Test
    void aceitaDecimalComVirgulaQuandoNaoHaSeparadorDeMilhar() {
        byte[] conteudo = csv("""
                Data,Valor,Saldo,Descricao
                10/02/2026,1590,00,Deposito
                """);
        // split(",") quebra "1590,00" em duas colunas; cols[1]="1590" -> parseia como 1590
        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(conteudo);

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("1590");
    }

    @Test
    void descartaSilenciosamenteLinhasComPoucasColunasOuValorInvalido() {
        byte[] conteudo = csv("""
                Data,Valor,Saldo,Descricao
                so,duas
                03/01/2026,abc,0,Invalida
                04/01/2026,15.00,0,Ok
                """);

        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(conteudo);

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getDescricao()).isEqualTo("Ok");
    }

    @Test
    void parseiaDataNoFormatoDdMmYyyy() {
        byte[] conteudo = csv("Data,Valor,Saldo,Descricao\n15/03/2026,1.00,0,X\n");

        List<ExtratoContaCorrenteDTO> itens = ExtratoContaCorrenteCSVParser.parse(conteudo);

        assertThat(new SimpleDateFormat("yyyy-MM-dd").format(itens.get(0).getDataLancamento()))
                .isEqualTo("2026-03-15");
    }
}
