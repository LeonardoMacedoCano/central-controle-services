package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoMovimentacaoB3DTO;
import br.com.lcano.fluxocaixa.exception.ExtratoException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExtratoMovimentacaoB3CSVParser {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static List<ExtratoMovimentacaoB3DTO> parse(byte[] conteudo) {
        List<ExtratoMovimentacaoB3DTO> itens = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(conteudo), StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] cols = line.split(";");
                if (cols.length < 7) continue;

                try {
                    String tipoOperacao = cols[0].trim();
                    java.util.Date dataMovimentacao = sdf.parse(cols[1].trim());
                    String tipoMovimentacao = cols[2].trim();
                    String produto = cols[3].trim();
                    BigDecimal quantidade = parseMonetario(cols[4].trim());
                    BigDecimal precoUnitario = parseMonetario(cols[5].trim());
                    BigDecimal precoTotal = parseMonetario(cols[6].trim());

                    itens.add(new ExtratoMovimentacaoB3DTO(
                            tipoOperacao, dataMovimentacao, tipoMovimentacao,
                            produto, quantidade, precoUnitario, precoTotal));
                } catch (ParseException | NumberFormatException e) {
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        return itens;
    }

    private static BigDecimal parseMonetario(String valor) {
        return new BigDecimal(
                valor.replace("R$", "")
                        .replace(".", "")
                        .replace(",", ".")
                        .trim()
        );
    }
}
