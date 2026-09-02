package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExtratoFaturaCartaoCSVParserTest {

    private static byte[] csv(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void parseiaLinhasComEComDataIso() {
        List<ExtratoFaturaCartaoDTO> itens = ExtratoFaturaCartaoCSVParser.parse(csv("""
                data,descricao,valor,categoria
                01/01/2026,Restaurante,89.90,Alimentacao
                2026-01-02,Uber,23.50,
                """));

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).getDescricao()).isEqualTo("Restaurante");
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("89.90");
        assertThat(itens.get(0).getCategoria()).isEqualTo("Alimentacao");
        assertThat(itens.get(1).getCategoria()).isNull();
    }

    @Test
    void aceitaValorBrasileiroEDescricaoComVirgula() {
        List<ExtratoFaturaCartaoDTO> itens = ExtratoFaturaCartaoCSVParser.parse(csv("""
                data,descricao,valor
                03/01/2026,"LOJA X, FILIAL 2","1.499,90"
                """));

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getDescricao()).isEqualTo("LOJA X, FILIAL 2");
        assertThat(itens.get(0).getValor()).isEqualByComparingTo("1499.90");
    }

    @Test
    void descartaLinhaComDataInvalidaSemQuebrar() {
        List<ExtratoFaturaCartaoDTO> itens = ExtratoFaturaCartaoCSVParser.parse(csv("""
                data,descricao,valor
                sem-data,Algo,10.00
                04/01/2026,Ok,12.00
                """));

        assertThat(itens).hasSize(1);
        assertThat(itens.get(0).getDescricao()).isEqualTo("Ok");
    }
}
