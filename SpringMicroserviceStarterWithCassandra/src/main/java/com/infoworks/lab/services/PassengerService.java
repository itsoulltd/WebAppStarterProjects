package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerRepository;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("passengerService")
public class PassengerService extends SimpleDataSource<String, Passenger> {

    private PassengerRepository repository;

    public PassengerService(PassengerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Passenger read(String key) {
        return repository.read(key);
    }

    @Override
    public Passenger[] readSync(int offset, int pageSize) {
        return repository.fetch(offset, pageSize).toArray(new Passenger[0]);
    }

    public List<Passenger> search(SearchQuery searchQuery) {
        return repository.search(searchQuery);
    }

    @Override
    public int size() {
        return repository.rowCount().getCount().intValue();
    }

    @Override
    public void put(String key, Passenger passenger) {
        if (passenger == null || key == null || key.isEmpty()) return;
        repository.insert(passenger);
    }

    @Override
    public Passenger replace(String key, Passenger passenger) {
        return repository.update(passenger, key);
    }

    @Override
    public Passenger remove(String key) {
        Passenger existing = read(key);
        if (existing != null) {
            repository.delete(key);
        }
        return existing;
    }
}
