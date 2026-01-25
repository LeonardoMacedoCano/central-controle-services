package br.com.lcano.fluxocaixa.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RsqlSpecUtil {

    private static final Set<ComparisonOperator> OPERATORS =
            Arrays.stream(RsqlSearchOperation.values())
                    .map(RsqlSearchOperation::toOperator)
                    .collect(Collectors.toCollection(HashSet::new));

    public static <T> Specification<T> fromFilter(String filter) {
        if (filter == null || filter.isBlank()) {
            return null;
        }

        RSQLParser parser = new RSQLParser(OPERATORS);
        Node rootNode = parser.parse(filter);

        RsqlSpecificationBuilder<T> builder = new RsqlSpecificationBuilder<>();
        return rootNode.accept(builder);
    }
}
