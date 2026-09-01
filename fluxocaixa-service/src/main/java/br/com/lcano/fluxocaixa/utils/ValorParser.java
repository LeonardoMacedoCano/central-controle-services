package br.com.lcano.fluxocaixa.utils;

import java.math.BigDecimal;

/**
 * Converte valores monetários textuais para {@link BigDecimal}, aceitando os
 * formatos que aparecem em extratos brasileiros e exportações en-US:
 * {@code "1234.56"}, {@code "1234,56"}, {@code "1.234,56"}, {@code "-1.234,56"},
 * {@code "R$ 1.234,56"}, {@code "1,234.56"}, {@code "(1.234,56)"} (negativo entre
 * parênteses).
 *
 * <p>Regra do separador decimal: quando há vírgula e ponto, o que aparecer por
 * último é o decimal e o outro é separador de milhar; quando há só vírgula, ela
 * é o decimal; quando há só ponto (ou nenhum separador), o texto já está no
 * formato aceito por {@link BigDecimal}.
 */
public final class ValorParser {

    private ValorParser() {
    }

    public static BigDecimal parse(String raw) {
        if (raw == null) {
            throw new NumberFormatException("valor nulo");
        }

        String s = raw.replace("R$", "").replaceAll("[\\s\\u00A0]", "");
        if (s.isEmpty()) {
            throw new NumberFormatException("valor vazio");
        }

        boolean negativoParenteses = s.startsWith("(") && s.endsWith(")");
        if (negativoParenteses) {
            s = s.substring(1, s.length() - 1);
        }

        int lastComma = s.lastIndexOf(',');
        int lastDot = s.lastIndexOf('.');

        if (lastComma >= 0 && lastDot >= 0) {
            if (lastComma > lastDot) {
                s = s.replace(".", "").replace(',', '.');
            } else {
                s = s.replace(",", "");
            }
        } else if (lastComma >= 0) {
            s = s.replace(',', '.');
        }

        BigDecimal valor = new BigDecimal(s);
        return negativoParenteses ? valor.negate() : valor;
    }
}
