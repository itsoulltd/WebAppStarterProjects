package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.domain.mongo.events.UserEventListener;
import com.infoworks.lab.domain.repositories.UserRepository;
import com.infoworks.lab.services.GeneratorService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserService extends SimpleDataSource<String, User> {

    private UserRepository repository;
    private GeneratorService genService;

    public UserService(UserRepository repository
            , @Qualifier("seqGenService") GeneratorService genService) {
        this.repository = repository;
        this.genService = genService;
    }

    @Override
    public User read(String key) {
        List<User> res = repository.findByName(key);
        return res != null && res.size() > 0 ? res.get(0) : null;
    }

    @Override
    public User[] readSync(int offset, int pageSize) {
        Page<User> finds = repository.findAll(PageRequest.of(offset, pageSize));
        return finds.getContent().toArray(new User[0]);
    }

    @Override
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }

    @Override
    public void put(String key, User user) {
        add(user);
    }

    @Override
    public String add(User user) throws RuntimeException {
        long key = genService.getNext(UserEventListener.SEQUENCE_KEY);
        user.setId(Long.valueOf(key).intValue());
        repository.save(user);
        return Long.toString(key);
    }

    @Override
    public User replace(String key, User user) {
        User existing = read(key);
        if (existing != null && user != null) {
            user.setId(existing.getId());
            existing.unmarshallingFromMap(user.marshallingToMap(true), true);
            repository.save(existing);
        }
        return existing;
    }

    @Override
    public User remove(String key) {
        User existing = read(key);
        if (existing != null) {
            repository.deleteById(existing.getId());
        }
        return existing;
    }
}
