package com.infoworks.lab.domain.repositories;

import com.infoworks.entity.Entity;
import com.infoworks.lab.domain.models.ItemCount;
import com.infoworks.sql.query.pagination.SearchQuery;

import java.util.ArrayList;
import java.util.List;

public interface RestRepository<E extends Entity, ID> {
    Class<E> getEntityType();
    String getPrimaryKeyName();
    ItemCount rowCount() throws RuntimeException;

    /**
     * @param offset
     * @param limit
     * @return
     * @throws RuntimeException
     */
    List<E> fetch(Integer offset, Integer limit) throws RuntimeException;

    /**
     * @param entity
     * @return
     * @throws RuntimeException
     */
    E insert(E entity) throws RuntimeException;

    /**
     * @param entity
     * @param id
     * @return
     * @throws RuntimeException
     */
    E update(E entity, ID id) throws RuntimeException;

    /**
     * @param id
     * @return
     * @throws RuntimeException
     */
    boolean delete(ID id) throws RuntimeException;
    default List<E> search(SearchQuery searchQuery) {return new ArrayList<>();}
}
