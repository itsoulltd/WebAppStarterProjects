package com.infoworks.lab.domain.repositories;

import com.infoworks.cql.CQLExecutor;
import com.infoworks.cql.query.CQLQuery;
import com.infoworks.cql.query.CQLSelectQuery;
import com.infoworks.entity.Entity;
import com.infoworks.lab.domain.models.ItemCount;
import com.infoworks.sql.query.QueryType;
import com.infoworks.sql.query.SQLScalarQuery;
import com.infoworks.sql.query.pagination.SearchQuery;

import java.sql.SQLException;
import java.util.List;

public interface CqlRepository<E extends Entity, ID> extends iGenericRepository<E, ID> {

    CQLExecutor getExecutor();

    /**
     * @param searchQuery
     * @return
     */
    default List<E> search(SearchQuery searchQuery) {
        try {
            CQLSelectQuery query;
            CQLQuery.Builder queryBuilder = new CQLQuery.Builder(QueryType.SELECT);
            if (searchQuery.getProperties().isEmpty()){
                query = queryBuilder.columns().from(getEntityType()).build();
            }else {
                query = queryBuilder.columns().from(getEntityType()).where(searchQuery.getPredicate()).build();
            }
            List<E> items = getExecutor().executeSelect(query, getEntityType());
            int fromIdx = fromIdx(searchQuery);
            int toIdx = toIdx(searchQuery, fromIdx, items.size());
            List<E> res = items.subList(fromIdx, toIdx);
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param offset
     * @param limit
     * @return
     * @throws RuntimeException
     */
    @Override
    default List<E> fetch(Integer offset, Integer limit) throws RuntimeException {
        try {
            CQLSelectQuery query = new CQLQuery.Builder(QueryType.SELECT)
                    .columns()
                    .from(getEntityType())
                    .build();
            List<E> items = getExecutor().executeSelect(query, getEntityType());
            int fromIdx = offset;
            if (fromIdx < 0) fromIdx = 0;
            int toIdx = fromIdx + limit;
            if (items.size() < toIdx) toIdx = items.size();
            List<E> res = items.subList(fromIdx, toIdx);
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @return
     * @throws RuntimeException
     */
    @Override
    default ItemCount rowCount() throws RuntimeException {
        try {
            SQLScalarQuery query = new CQLQuery.Builder(QueryType.COUNT).columns().on(getEntityType()).build();
            int count = getExecutor().getScalarValue(query);
            ItemCount ic = new ItemCount();
            ic.setCount(Integer.valueOf(count).longValue());
            return ic;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
