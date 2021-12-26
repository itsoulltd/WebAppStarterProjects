package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.repository.RestRepository;
import com.it.soul.lab.cql.CQLExecutor;
import com.it.soul.lab.cql.query.CQLQuery;
import com.it.soul.lab.cql.query.CQLSelectQuery;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.models.Where;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CqlRepository<E extends Entity, ID> extends RestRepository<E, ID> {

    CQLExecutor getExecutor();

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
            int fromIdx = (searchQuery.getPage() - 1) * searchQuery.getSize();
            if (fromIdx < 0) fromIdx = 0;
            int toIdx = fromIdx + searchQuery.getSize();
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

    @Override
    default List<E> fetch(Integer page, Integer limit) throws RuntimeException {
        try {
            CQLSelectQuery query = new CQLQuery.Builder(QueryType.SELECT)
                    .columns()
                    .from(getEntityType())
                    .build();
            List<E> items = getExecutor().executeSelect(query, getEntityType());
            int fromIdx = (page - 1) * limit;
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

    default E read(ID key) {
        List<E> res = null;
        try {
            res = Entity.read(getEntityType(), getExecutor(), new Where(getPrimaryKeyName()).isEqualTo(key));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return res != null && res.size() > 0 ? res.get(0) : null;
    }

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

    @Override
    default E insert(E e) throws RuntimeException {
        if (e == null) return null;
        try {
            e.insert(getExecutor());
        } catch (SQLException err) {
            throw new RuntimeException(err.getMessage());
        }
        return null;
    }

    @Override
    default E update(E e, ID id) throws RuntimeException {
        E existing = read(id);
        if (existing != null && e != null) {
            Map<String, Object> eData = e.marshallingToMap(true);
            eData.remove(getPrimaryKeyName());
            existing.unmarshallingFromMap(eData, true);
            try {
                existing.update(getExecutor());
            } catch (SQLException err) {
                throw new RuntimeException(err.getMessage());
            }
        }
        return existing;
    }

    @Override
    default boolean delete(ID id) throws RuntimeException {
        E existing = read(id);
        if (existing != null) {
            try {
                return existing.delete(getExecutor());
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
    }
}
