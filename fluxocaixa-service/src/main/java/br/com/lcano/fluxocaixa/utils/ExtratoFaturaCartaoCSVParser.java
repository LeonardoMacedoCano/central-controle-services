package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
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

public class ExtratoFaturaCartaoCSVParser {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static List<ExtratoFaturaCartaoDTO> parse(byte[] conteudo) {
        List<ExtratoFaturaCartaoDTO> itens = new ArrayList<>();
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
                if (cols.length < 3) continue;

                try {
                    java.util.Date dataLancamento = sdf.parse(cols[0].trim());
                    String descricao = cols[1].trim();
                    BigDecimal valor = new BigDecimal(cols[2].trim().replace(",", "."));
                    String categoria = cols.length > 3 ? cols[3].trim() : null;
                    if (categoria != null && categoria.isEmpty()) categoria = null;

                    itens.add(new ExtratoFaturaCartaoDTO(dataLancamento, descricao, valor, categoria));
                } catch (ParseException | NumberFormatException e) {
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        return itens;
    }
}
