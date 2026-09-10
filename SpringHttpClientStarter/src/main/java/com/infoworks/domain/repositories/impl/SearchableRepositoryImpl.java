package com.infoworks.domain.repositories.impl;

import com.infoworks.domain.repositories.SearchableRepository;
import com.infoworks.sql.query.models.Logic;
import com.infoworks.sql.query.pagination.SearchQuery;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class SearchableRepositoryImpl<T, ID> implements SearchableRepository<T, ID> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<T> search(SearchQuery searchQuery, Class<T> type) {
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = cBuilder.createQuery(type);
        Root<T> bean = cQuery.from(type);
        //Create predicates from SearchQuery.QueryProperty:
        Map<String, Predicate> predicates = SearchableRepository.getPredicatesFrom(searchQuery, cBuilder, bean);
        //Setup CriteriaQuery For Select:
        //Since this is a searching impl, So fallback logic should be OR.
        Logic running = Logic.OR;
        Predicate curry = null;
        for (SearchQuery.QueryProperty property : searchQuery.getProperties()){
            if (curry != null) {
                if (running == Logic.AND) {
                    curry = cBuilder.and(curry, predicates.get(property.getKey()));
                } else {
                    curry = cBuilder.or(curry, predicates.get(property.getKey()));
                }
            } else {
                curry = predicates.get(property.getKey());
            }
            running = Optional.ofNullable(property.getLogic()).orElse(Logic.OR);
        }
        if(curry != null) cQuery.select(bean).where(curry);
        //else cQuery.select(bean).where(cBuilder.or(predicates.values().toArray(new Predicate[predicates.size()])));
        //Apply sorting in CriteriaQuery:
        List<Order> sortBy = SearchableRepository.getSortOrdersFrom(searchQuery, cBuilder, bean);
        if (sortBy.size() > 0)
            cQuery.orderBy(sortBy);
        //Create & Setup Persistence Query For Fetch:
        Query persistenceQuery = entityManager.createQuery(cQuery);
        SearchableRepository.setOffsetAndLimitInto(persistenceQuery, searchQuery);
        return persistenceQuery.getResultList();
    }

    @Override
    public void search(SearchQuery query, Class<T> type, int pageCount, Consumer<List<T>> consumer) {
        int pageSize = (query.getSize() <= 0) ? 10 : query.getSize();
        query.setSize(pageSize); //update with validated pageSize.
        pageCount = (pageCount <= 0) ? 1 : pageCount;
        int start = 1; //SearchableRepository page-number is 1-based.
        while (start <= pageCount) {
            query.setPage(start);
            List<T> result = search(query, type);
            if (consumer != null) {
                if (!result.isEmpty()) consumer.accept(result);
                else { consumer.accept(result); break; }
            }
            //Next page:
            start++;
        }
    }
}
