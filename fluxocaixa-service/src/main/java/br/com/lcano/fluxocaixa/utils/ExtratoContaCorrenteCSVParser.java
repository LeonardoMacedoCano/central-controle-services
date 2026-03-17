package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoContaCorrenteDTO;
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

public class ExtratoContaCorrenteCSVParser {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static List<ExtratoContaCorrenteDTO> parse(byte[] conteudo) {
        List<ExtratoContaCorrenteDTO> itens = new ArrayList<>();
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

                String[] cols = line.split(",");
                if (cols.length < 4) continue;

                try {
                    java.util.Date dataLancamento = sdf.parse(cols[0].trim());
                    BigDecimal valor = new BigDecimal(cols[1].trim().replace(",", "."));
                    String descricao = cols[3].trim();
                    itens.add(new ExtratoContaCorrenteDTO(dataLancamento, valor, descricao));
                } catch (ParseException | NumberFormatException ignored) {
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        return itens;
    }
}
