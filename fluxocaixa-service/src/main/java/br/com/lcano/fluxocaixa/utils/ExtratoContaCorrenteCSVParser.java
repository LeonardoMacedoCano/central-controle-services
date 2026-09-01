package br.com.lcano.fluxocaixa.utils;

import br.com.lcano.fluxocaixa.dto.ExtratoContaCorrenteDTO;
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

public class ExtratoContaCorrenteCSVParser {

    private static final Logger log = LoggerFactory.getLogger(ExtratoContaCorrenteCSVParser.class);
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static List<ExtratoContaCorrenteDTO> parse(byte[] conteudo) {
        List<ExtratoContaCorrenteDTO> itens = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);

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
                if (cols.size() < 4) {
                    rejeitadas++;
                    log.warn("Extrato conta corrente: linha {} ignorada (colunas insuficientes): {}", numeroLinha, line);
                    continue;
                }

                try {
                    java.util.Date dataLancamento = sdf.parse(cols.get(0).trim());
                    BigDecimal valor = ValorParser.parse(cols.get(1));
                    String descricao = cols.get(3).trim();
                    itens.add(new ExtratoContaCorrenteDTO(dataLancamento, valor, descricao));
                } catch (Exception e) {
                    rejeitadas++;
                    log.warn("Extrato conta corrente: linha {} ignorada ({}): {}", numeroLinha, e.getMessage(), line);
                }
            }
        } catch (Exception e) {
            throw new ExtratoException.ErroLeituraArquivo(e.getMessage());
        }

        if (rejeitadas > 0) {
            log.warn("Extrato conta corrente: {} linha(s) nao reconhecida(s), {} importada(s).", rejeitadas, itens.size());
        }
        return itens;
    }
}
