package br.com.lcano.fluxocaixa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Apoio para leitura de CSV de extrato: detecção conservadora de delimitador e
 * quebra de linha respeitando campos entre aspas duplas ({@code "campo, com vírgula"},
 * {@code ""} como aspas escapada).
 */
public final class CsvSupport {

    private CsvSupport() {
    }

    /**
     * Escolhe o delimitador a partir da linha de cabeçalho. Só sai do padrão
     * {@code ','} quando ele não aparece: assim um arquivo separado por vírgula
     * nunca é reinterpretado.
     */
    public static char detectarDelimitador(String header) {
        if (header == null) {
            return ',';
        }
        long virgulas = header.chars().filter(c -> c == ',').count();
        if (virgulas > 0) {
            return ',';
        }
        long pontoVirgulas = header.chars().filter(c -> c == ';').count();
        if (pontoVirgulas > 0) {
            return ';';
        }
        long tabs = header.chars().filter(c -> c == '\t').count();
        return tabs > 0 ? '\t' : ',';
    }

    public static List<String> split(String line, char delim) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == delim) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }
}
