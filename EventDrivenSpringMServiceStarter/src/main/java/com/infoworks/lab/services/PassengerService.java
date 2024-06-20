package com.infoworks.lab.services;

import com.infoworks.lab.cache.MemCache;
import com.infoworks.lab.datasources.RedissonDataSource;
import com.infoworks.lab.domain.entities.Passenger;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service("passengerService")
public class PassengerService extends MemCache<Passenger> {

    private RedissonClient client;

    public PassengerService(RedissonClient client) {
        super(new RedissonDataSource(client), Passenger.class);
        this.client = client;
    }

    /**
     * To make a short key, we choose prefix: 'Class.getSimpleName() + "_"
     * @return
     */
    protected String maskPrefix() {
        String fullClsName = Passenger.class.getSimpleName();
        return fullClsName + "_";
    }

    public String maskedKey(String key) {
        return maskPrefix() + key;
    }

    @Override
    public void put(String key, Passenger passenger) {
        super.put(maskedKey(key), passenger);
    }

    @Override
    public String add(Passenger e) {
        String key = String.valueOf(e.hashCode());
        this.put(maskedKey(key), e);
        return key;
    }

    @Override
    public Passenger read(String key) {
        return super.read(maskedKey(key));
    }

    @Override
    public Passenger remove(String key) {
        return super.remove(maskedKey(key));
    }

    @Override
    public Passenger replace(String key, Passenger passenger) {
        return super.replace(maskedKey(key), passenger);
    }

    @Override
    public Passenger[] readSync(int offset, int pageSize) {
        if (offset < 0) offset = 0;
        //TODO (CAUTION): fetch client.getKeys() in Batch (in-future) to avoid memory-dumb.
        RKeys keys = client.getKeys();
        List<String> targetMaskedKeys = keys.getKeysStream()
                .filter(key -> key.startsWith(maskPrefix()))
                .collect(Collectors.toList());
        targetMaskedKeys.sort(String::compareTo);
        //Pagination logic:
        List<Passenger> results = new ArrayList<>();
        List<String> subMaskedKeys;
        if (targetMaskedKeys.size() > (offset + pageSize)) {
             subMaskedKeys = targetMaskedKeys.subList(offset, (offset + pageSize));
        } else {
            if (offset > targetMaskedKeys.size()) offset = 0;
            subMaskedKeys = targetMaskedKeys.subList(offset, targetMaskedKeys.size());
        }
        //Where logic:
        for (String maskedKey : subMaskedKeys) {
            results.add(super.read(maskedKey));
        }
        return results.toArray(new Passenger[0]);
    }

    @Override
    public void readAsync(int offset, int pageSize, Consumer<Passenger[]> consumer) {
        if (consumer != null)
            consumer.accept(readSync(offset, pageSize));
    }
}
