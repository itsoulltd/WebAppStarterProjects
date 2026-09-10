package com.infoworks.domain.repositories;

import com.infoworks.sql.query.pagination.SearchQuery;
import com.infoworks.sql.query.pagination.SortDescriptor;
import com.infoworks.sql.query.pagination.SortOrder;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.function.Consumer;

@Transactional(readOnly=true)
public interface SearchableRepository<T, ID> {
    List<T> search(SearchQuery query, Class<T> type);

    /**
     * PageCount means number of times the callback is executed. If less than 0, then set to 1.
     * If higher than the actual counts, the then executed as exhaustive + 1; The last execution will have empty result.
     * So that end of execution can be handled.
     * @param query
     * @param type
     * @param pageCount
     * @param consumer
     */
    void search(SearchQuery query, Class<T> type, int pageCount, Consumer<List<T>> consumer);

    static <T> Map<String, Predicate> getPredicatesFrom(SearchQuery query, CriteriaBuilder cBuilder, Root<T> bean, String... skipKeys) {
        Map<String, Predicate> predicates = new HashMap();
        List<String> skipList = Arrays.asList(skipKeys);
        query.getProperties().stream()
                .filter(prop -> !skipList.contains(prop.getKey()))
                .filter(prop -> prop.getKey() != null)
                .forEach(prop -> {
                    Path<String> propKey = bean.get(prop.getKey());
                    String propValue = prop.getValue();
                    switch (prop.getOperator()){
                        case GREATER_THAN:
                            predicates.put(prop.getKey(), cBuilder.greaterThan(propKey, propValue));
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            predicates.put(prop.getKey(), cBuilder.greaterThanOrEqualTo(propKey, propValue));
                            break;
                        case LESS_THAN:
                            predicates.put(prop.getKey(), cBuilder.lessThan(propKey, propValue));
                            break;
                        case LESS_THAN_OR_EQUAL:
                            predicates.put(prop.getKey(), cBuilder.lessThanOrEqualTo(propKey, propValue));
                            break;
                        case IN:
                            Object[] inValues = prop.getValue()
                                    .replace("'", "")
                                    .split(",");
                            predicates.put(prop.getKey(), cBuilder.in(propKey).in(inValues));
                            break;
                        case LIKE:
                            predicates.put(prop.getKey(), cBuilder.like(propKey, propValue));
                            break;
                        case NOT_LIKE:
                            predicates.put(prop.getKey(), cBuilder.notLike(propKey, propValue));
                            break;
                        case IS_NULL:
                            predicates.put(prop.getKey(), cBuilder.isNull(propKey));
                            break;
                        case NOT_NULL:
                            predicates.put(prop.getKey(), cBuilder.isNotNull(propKey));
                            break;
                        default:
                            predicates.put(prop.getKey(), cBuilder.equal(propKey, propValue));
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
