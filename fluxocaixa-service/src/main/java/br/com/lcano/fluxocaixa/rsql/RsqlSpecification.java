package br.com.lcano.fluxocaixa.rsql;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class RsqlSpecification<T> implements Specification<T> {

    private final RsqlSearchCriteria criteria;

    public RsqlSpecification(RsqlSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String value = criteria.getValues().getFirst();

        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(root.get(criteria.getProperty()), value);
            case NOT_EQUAL -> builder.notEqual(root.get(criteria.getProperty()), value);
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getProperty()), value);
            case LESS_THAN -> builder.lessThan(root.get(criteria.getProperty()), value);
            case GREATER_THAN_OR_EQUAL -> builder.greaterThanOrEqualTo(root.get(criteria.getProperty()), value);
            case LESS_THAN_OR_EQUAL -> builder.lessThanOrEqualTo(root.get(criteria.getProperty()), value);
            case IN -> root.get(criteria.getProperty()).in(criteria.getValues());
            case NOT_IN -> builder.not(root.get(criteria.getProperty()).in(criteria.getValues()));
            case LIKE -> builder.like(builder.lower(root.get(criteria.getProperty())), "%" + value.toLowerCase(Locale.ROOT) + "%");
        };
    }
}
