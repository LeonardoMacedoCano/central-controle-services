package br.com.lcano.fluxocaixa.utils;

import java.math.BigDecimal;

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
