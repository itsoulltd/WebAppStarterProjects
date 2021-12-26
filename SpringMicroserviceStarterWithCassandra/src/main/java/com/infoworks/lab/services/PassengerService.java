package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.cql.CQLExecutor;
import com.it.soul.lab.cql.query.CQLQuery;
import com.it.soul.lab.cql.query.CQLSelectQuery;
import com.it.soul.lab.data.simple.SimpleDataSource;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.models.Where;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("passengerService")
public class PassengerService extends SimpleDataSource<String, Passenger> {

    private CQLExecutor repository;

    public PassengerService(CQLExecutor repository) {
        this.repository = repository;
    }

    @Override
    public Passenger read(String key) {
        List<Passenger> res = null;
        try {
            res = Passenger.read(Passenger.class, repository, new Where("uuid").isEqualTo(key));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res != null && res.size() > 0 ? res.get(0) : null;
    }

    @Override
    public Passenger[] readSync(int offset, int pageSize) {
        try {
            CQLSelectQuery query = new CQLQuery.Builder(QueryType.SELECT)
                    .columns()
                    .from(Passenger.class)
                    .build();
            List<Passenger> items = repository.executeSelect(query, Passenger.class);
            int fromIdx = offset;
            int toIdx = fromIdx + pageSize;
            List<Passenger> res = items.subList(fromIdx, toIdx);
            return res.toArray(new Passenger[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return new Passenger[0];
    }

    public List<Passenger> search(SearchQuery searchQuery) {
        try {
            CQLSelectQuery query;
            CQLQuery.Builder queryBuilder = new CQLQuery.Builder(QueryType.SELECT);
            if (searchQuery.getPredicate() == null){
                query = queryBuilder.columns().from(Passenger.class).build();
            }else {
                query = queryBuilder.columns().from(Passenger.class).where(searchQuery.getPredicate()).build();
            }
            List<Passenger> items = repository.executeSelect(query, Passenger.class);
            int fromIdx = searchQuery.getPage() * searchQuery.getSize();
            int toIdx = fromIdx + searchQuery.getSize();
            List<Passenger> res = items.subList(fromIdx, toIdx);
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public int size() {
        SQLScalarQuery query = new CQLQuery.Builder(QueryType.COUNT).columns().on(Passenger.class).build();
        try {
            int count = repository.getScalarValue(query);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void put(String key, Passenger passenger) {
        if (passenger == null || key == null || key.isEmpty()) return;
        if (read(key) != null) return;
        try {
            passenger.insert(repository);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Passenger replace(String key, Passenger passenger) {
        Passenger existing = read(key);
        if (existing != null && passenger != null) {
            passenger.setUuid(existing.getUuid());
            existing.unmarshallingFromMap(passenger.marshallingToMap(true), true);
            try {
                existing.update(repository);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return existing;
    }

    @Override
    public Passenger remove(String key) {
        Passenger existing = read(key);
        if (existing != null) {
            try {
                existing.delete(repository);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return existing;
    }
}
