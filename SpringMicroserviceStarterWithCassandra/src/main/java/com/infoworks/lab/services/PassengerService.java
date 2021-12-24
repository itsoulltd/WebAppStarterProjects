package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Passenger;
import com.it.soul.lab.cql.CQLExecutor;
import com.it.soul.lab.cql.query.CQLQuery;
import com.it.soul.lab.data.simple.SimpleDataSource;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.models.Where;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
            res = Passenger.read(Passenger.class, repository, new Where("id").isEqualTo(key));
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
        /*Page<Passenger> finds = repository.findAll(PageRequest.of(offset, pageSize));
        return finds.getContent().toArray(new Passenger[0]);*/
        return new Passenger[0];
    }

    @Override
    public int size() {
        SQLScalarQuery query = new CQLQuery.Builder(QueryType.COUNT).columns().on(Passenger.class).build();
        try {
            //return Long.valueOf(repository.count()).intValue();
            int count = repository.getScalarValue(query);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void put(String key, Passenger passenger) {
        if (passenger == null) return;
        //repository.save(passenger);
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
            passenger.setId(existing.getId());
            existing.unmarshallingFromMap(passenger.marshallingToMap(true), true);
            //repository.save(existing);
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
            //repository.deleteById(existing.getId());
            try {
                existing.delete(repository);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return existing;
    }
}
