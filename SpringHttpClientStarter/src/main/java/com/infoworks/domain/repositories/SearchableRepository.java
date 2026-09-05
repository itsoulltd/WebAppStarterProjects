package com.infoworks.domain.repositories;

import com.infoworks.sql.query.pagination.*;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional(readOnly=true)
public interface SearchableRepository<T, ID> {
    List<T> search(SearchQuery query, Class<T> type);
    static <T> List<Predicate> getPredicatesFrom(SearchQuery query, CriteriaBuilder cBuilder, Root<T> bean, String... skipKeys) {
        List<Predicate> predicates = new ArrayList<>();
        List<String> skipList = Arrays.asList(skipKeys);
        query.getProperties().stream()
                .filter(prop -> !skipList.contains(prop.getKey()))
                .filter(prop -> prop.getKey() != null)
                .forEach(prop -> {
                    Path<String> propKey = bean.get(prop.getKey());
                    String propValue = prop.getValue();
                    switch (prop.getOperator()){
                        case GREATER_THAN:
                            predicates.add(cBuilder.greaterThan(propKey, propValue));
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            predicates.add(cBuilder.greaterThanOrEqualTo(propKey, propValue));
                            break;
                        case LESS_THAN:
                            predicates.add(cBuilder.lessThan(propKey, propValue));
                            break;
                        case LESS_THAN_OR_EQUAL:
                            predicates.add(cBuilder.lessThanOrEqualTo(propKey, propValue));
                            break;
                        case IN:
                            Object[] inValues = prop.getValue()
                                    .replace("'", "")
                                    .split(",");
                            predicates.add(cBuilder.in(propKey).in(inValues));
                            break;
                        case LIKE:
                            predicates.add(cBuilder.like(propKey, propValue));
                            break;
                        case NOT_LIKE:
                            predicates.add(cBuilder.notLike(propKey, propValue));
                            break;
                        case IS_NULL:
                            predicates.add(cBuilder.isNull(propKey));
                            break;
                        case NOT_NULL:
                            predicates.add(cBuilder.isNotNull(propKey));
                            break;
                        default:
                            predicates.add(cBuilder.equal(propKey, propValue));
                    }
                });
        return predicates;
    }
    static <T> List<Order> getSortOrdersFrom(SearchQuery query, CriteriaBuilder cBuilder, Root<T> bean) {
        List<Order> sortBy = new ArrayList<>();
        SortDescriptor descriptor = query.getDescriptors().size() > 0
                ? query.getDescriptors().get(0)
                : null;
        if (descriptor != null) {
            SortOrder order = descriptor.getOrder();
            descriptor.getKeys().forEach(sortProperty -> {
                Order sortOrder = order == SortOrder.ASC
                        ? cBuilder.asc(bean.get(sortProperty))
                        : cBuilder.desc(bean.get(sortProperty));
                sortBy.add(sortOrder);
            });
        }
        return sortBy;
    }
    static Query setOffsetAndLimitInto(Query mQuery, SearchQuery query) {
        int size = query.getSize();
        if (size <= 0) size = 10;
        int page = query.getPage();
        if (page <= 0) page = 1;
        int offset = (page - 1) * size;
        mQuery.setFirstResult(offset);
        mQuery.setMaxResults(size);
        return mQuery;
    }
}
