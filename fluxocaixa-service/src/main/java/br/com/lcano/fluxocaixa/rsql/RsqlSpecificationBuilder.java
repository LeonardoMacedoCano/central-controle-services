package br.com.lcano.fluxocaixa.rsql;

import cz.jirutka.rsql.parser.ast.*;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RsqlSpecificationBuilder<T> implements RSQLVisitor<Specification<T>, Void> {

    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return node.getChildren().stream()
                .map(n -> n.accept(this))
                .reduce(Specification::and)
                .orElse(null);
    }

    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return node.getChildren().stream()
                .map(n -> n.accept(this))
                .reduce(Specification::or)
                .orElse(null);
    }

    @Override
    public Specification<T> visit(ComparisonNode node, Void param) {
        RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(node.getOperator().getSymbol());
        List<String> arguments = node.getArguments();
        RsqlSearchCriteria criteria = new RsqlSearchCriteria(node.getSelector(), operation, arguments);
        return new RsqlSpecification<>(criteria);
    }
}
