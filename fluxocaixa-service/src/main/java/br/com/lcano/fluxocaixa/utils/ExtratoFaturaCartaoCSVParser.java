package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
import br.com.lcano.fluxocaixa.exception.ExtratoException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExtratoFaturaCartaoCSVParser {

    private static final String[] DATE_FORMATS = {"dd/MM/yyyy", "yyyy-MM-dd"};

    public static List<ExtratoFaturaCartaoDTO> parse(byte[] conteudo) {
        List<ExtratoFaturaCartaoDTO> itens = new ArrayList<>();

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

                String[] cols = line.split(",");
                if (cols.length < 3) continue;

                try {
                    java.util.Date dataLancamento = parseData(cols[0].trim());
                    if (dataLancamento == null) continue;
                    String descricao = cols[1].trim();
                    BigDecimal valor = new BigDecimal(cols[2].trim().replace(",", "."));
                    String categoria = cols.length > 3 ? cols[3].trim() : null;
                    if (categoria != null && categoria.isEmpty()) categoria = null;

                    itens.add(new ExtratoFaturaCartaoDTO(dataLancamento, descricao, valor, categoria));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        return itens;
    }

    private static java.util.Date parseData(String valor) {
        for (String formato : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formato);
                sdf.setLenient(false);
                return sdf.parse(valor);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
