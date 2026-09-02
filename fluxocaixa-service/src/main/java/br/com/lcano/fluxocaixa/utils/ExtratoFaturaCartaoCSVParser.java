package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoFaturaCartaoDTO;
import br.com.lcano.fluxocaixa.exception.ExtratoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExtratoFaturaCartaoCSVParser {

    private static final Logger log = LoggerFactory.getLogger(ExtratoFaturaCartaoCSVParser.class);
    private static final String[] DATE_FORMATS = {"dd/MM/yyyy", "yyyy-MM-dd"};

    public static List<ExtratoFaturaCartaoDTO> parse(byte[] conteudo) {
        List<ExtratoFaturaCartaoDTO> itens = new ArrayList<>();
        int rejeitadas = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(conteudo), StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            char delim = CsvSupport.detectarDelimitador(header);

            String line;
            int numeroLinha = 1;
            while ((line = reader.readLine()) != null) {
                numeroLinha++;
                if (line.isBlank()) {
                    continue;
                }

                List<String> cols = CsvSupport.split(line, delim);
                if (cols.size() < 3) {
                    rejeitadas++;
                    log.warn("Fatura cartao: linha {} ignorada (colunas insuficientes): {}", numeroLinha, line);
                    continue;
                }

                try {
                    java.util.Date dataLancamento = parseData(cols.get(0).trim());
                    if (dataLancamento == null) {
                        rejeitadas++;
                        log.warn("Fatura cartao: linha {} ignorada (data invalida): {}", numeroLinha, line);
                        continue;
                    }
                    String descricao = cols.get(1).trim();
                    BigDecimal valor = ValorParser.parse(cols.get(2));
                    String categoria = cols.size() > 3 ? cols.get(3).trim() : null;
                    if (categoria != null && categoria.isEmpty()) {
                        categoria = null;
                    }
                    itens.add(new ExtratoFaturaCartaoDTO(dataLancamento, descricao, valor, categoria));
                } catch (Exception e) {
                    rejeitadas++;
                    log.warn("Fatura cartao: linha {} ignorada ({}): {}", numeroLinha, e.getMessage(), line);
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        if (rejeitadas > 0) {
            log.warn("Fatura cartao: {} linha(s) nao reconhecida(s), {} importada(s).", rejeitadas, itens.size());
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
