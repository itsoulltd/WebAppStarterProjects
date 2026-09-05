package com.infoworks.domain.repositories.impl;

import com.infoworks.domain.repositories.SearchableRepository;
import com.infoworks.sql.query.pagination.SearchQuery;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import java.util.List;

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
        List<Predicate> predicates = SearchableRepository.getPredicatesFrom(searchQuery, cBuilder, bean);
        //Setup CriteriaQuery For Select:
        cQuery.select(bean)
                .where(cBuilder.or(predicates.toArray(new Predicate[predicates.size()])));
        //Apply sorting in CriteriaQuery:
        List<Order> sortBy = SearchableRepository.getSortOrdersFrom(searchQuery, cBuilder, bean);
        if (sortBy.size() > 0)
            cQuery.orderBy(sortBy);
        //Create & Setup Persistence Query For Fetch:
        Query persistenceQuery = entityManager.createQuery(cQuery);
        SearchableRepository.setOffsetAndLimitInto(persistenceQuery, searchQuery);
        return persistenceQuery.getResultList();
    }
}
