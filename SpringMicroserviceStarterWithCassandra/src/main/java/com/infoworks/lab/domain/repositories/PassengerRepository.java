package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.repository.CqlRepository;
import com.it.soul.lab.cql.CQLExecutor;
import org.springframework.stereotype.Repository;

@Repository
public class PassengerRepository implements CqlRepository<Passenger, String> {

    private CQLExecutor executor;

    public PassengerRepository(CQLExecutor executor) {
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
    public Class<Passenger> getEntityType() {
        return Passenger.class;
    }
}
