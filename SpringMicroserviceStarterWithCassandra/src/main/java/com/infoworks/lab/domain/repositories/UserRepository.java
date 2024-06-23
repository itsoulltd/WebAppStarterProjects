package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.rest.repository.CqlRepository;
import com.it.soul.lab.cql.CQLExecutor;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements CqlRepository<User, String> {

    private CQLExecutor executor;

    public UserRepository(CQLExecutor executor) {
        this.executor = executor;
    }

    @Override
    public CQLExecutor getExecutor() {
        return executor;
    }

    @Override
    public String getPrimaryKeyName() {
        return "uuid";
    }

    @Override
    public Class<User> getEntityType() {
        return User.class;
    }
}
