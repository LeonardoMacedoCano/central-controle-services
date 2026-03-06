package br.com.lcano.fluxocaixa.rsql;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Locale;

public class RsqlSpecification<T> implements Specification<T> {

    private final RsqlSearchCriteria criteria;

    public RsqlSpecification(RsqlSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(
            Root<T> root,
            @Nonnull CriteriaQuery<?> query,
            @Nonnull CriteriaBuilder builder
    ) {

        Path<?> path = root.get(criteria.getProperty());
        Class<?> fieldType = path.getJavaType();

        String rawValue = criteria.getValues().getFirst();
        Object value = convertValue(rawValue, fieldType);

        return switch (criteria.getOperation()) {
            case EQUAL -> builder.equal(path, value);
            case NOT_EQUAL -> builder.notEqual(path, value);

            case GREATER_THAN -> greaterThan(builder, path, value);
            case LESS_THAN -> lessThan(builder, path, value);
            case GREATER_THAN_OR_EQUAL -> greaterThanOrEqual(builder, path, value);
            case LESS_THAN_OR_EQUAL -> lessThanOrEqual(builder, path, value);

            case IN -> path.in(
                    criteria.getValues().stream()
                            .map(v -> convertValue(v, fieldType))
                            .toList()
            );
            case NOT_IN -> builder.not(path.in(
                    criteria.getValues().stream()
                            .map(v -> convertValue(v, fieldType))
                            .toList()
            ));

            case LIKE -> builder.like(
                    builder.lower(path.as(String.class)),
                    "%" + rawValue.toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    private Object convertValue(String value, Class<?> fieldType) {
        if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.valueOf(value);
        }

        if (fieldType == Timestamp.class) {
            LocalDate date = LocalDate.parse(value);
            return Timestamp.valueOf(date.atStartOfDay());
        }

        if (fieldType == Long.class || fieldType == long.class) {
            return Long.valueOf(value);
        }

        if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.valueOf(value);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate greaterThan(
            CriteriaBuilder builder,
            Path<?> path,
            Object value
    ) {
        return builder.greaterThan((Expression<Y>) path, (Y) value);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate lessThan(
            CriteriaBuilder builder,
            Path<?> path,
            Object value
    ) {
        return builder.lessThan((Expression<Y>) path, (Y) value);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate greaterThanOrEqual(
            CriteriaBuilder builder,
            Path<?> path,
            Object value
    ) {
        return builder.greaterThanOrEqualTo((Expression<Y>) path, (Y) value);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate lessThanOrEqual(
            CriteriaBuilder builder,
            Path<?> path,
            Object value
    ) {
        return builder.lessThanOrEqualTo((Expression<Y>) path, (Y) value);
    }
}
