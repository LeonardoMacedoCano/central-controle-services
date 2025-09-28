package br.com.lcano.fluxocaixa.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public enum RsqlSearchOperation {
    EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN,
    GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL,
    IN, NOT_IN, LIKE;

    public static RsqlSearchOperation getSimpleOperator(String operator) {
        return switch (operator) {
            case "==" -> EQUAL;
            case "!=" -> NOT_EQUAL;
            case "=gt=" -> GREATER_THAN;
            case "=lt=" -> LESS_THAN;
            case "=ge=" -> GREATER_THAN_OR_EQUAL;
            case "=le=" -> LESS_THAN_OR_EQUAL;
            case "=in=" -> IN;
            case "=out=" -> NOT_IN;
            case "=ilike=" -> LIKE;
            default -> throw new IllegalArgumentException("Operador desconhecido: " + operator);
        };
    }

    public ComparisonOperator toOperator() {
        return switch (this) {
            case EQUAL -> new ComparisonOperator("==");
            case NOT_EQUAL -> new ComparisonOperator("!=");
            case GREATER_THAN -> new ComparisonOperator("=gt=");
            case LESS_THAN -> new ComparisonOperator("=lt=");
            case GREATER_THAN_OR_EQUAL -> new ComparisonOperator("=ge=");
            case LESS_THAN_OR_EQUAL -> new ComparisonOperator("=le=");
            case IN -> new ComparisonOperator("=in=");
            case NOT_IN -> new ComparisonOperator("=out=");
            case LIKE -> new ComparisonOperator("=ilike=");
        };
    }
}
