package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerRepository;
import com.infoworks.lab.services.GeneratorService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("passengerService")
public class PassengerService extends SimpleDataSource<String, Passenger> {

    private PassengerRepository repository;
    private GeneratorService genService;

    public PassengerService(PassengerRepository repository
            , @Qualifier("seqGenService") GeneratorService genService) {
        this.repository = repository;
        this.genService = genService;
    }

    @Override
    public Passenger read(String key) {
        List<Passenger> res = repository.findByName(key);
        return res != null && res.size() > 0 ? res.get(0) : null;
    }

    @Override
    public Passenger[] readSync(int offset, int pageSize) {
        Page<Passenger> finds = repository.findAll(PageRequest.of(offset, pageSize));
        return finds.getContent().toArray(new Passenger[0]);
    }

    @Override
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }

    @Override
    public void put(String key, Passenger passenger) {
        add(passenger);
    }

    @Override
    public String add(Passenger passenger) throws RuntimeException {
        long key = genService.getNext("persona_seq");
        passenger.setId(Long.valueOf(key).intValue());
        repository.save(passenger);
        return Long.toString(key);
    }

    @Override
    public Passenger replace(String key, Passenger passenger) {
        Passenger existing = read(key);
        if (existing != null && passenger != null) {
            passenger.setId(existing.getId());
            existing.unmarshallingFromMap(passenger.marshallingToMap(true), true);
            repository.save(existing);
        }
        return existing;
    }

    @Override
    public Passenger remove(String key) {
        Passenger existing = read(key);
        if (existing != null) {
            repository.deleteById(existing.getId());
        }
        return existing;
    }
}
