package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.domain.repositories.UserRepository;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserService extends SimpleDataSource<String, User> {

    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User read(String key) {
        return repository.read(key);
    }

    @Override
    public User[] readSync(int offset, int pageSize) {
        return repository.fetch(offset, pageSize).toArray(new User[0]);
    }

    public List<User> search(SearchQuery searchQuery) {
        return repository.search(searchQuery);
    }

    @Override
    public int size() {
        return repository.rowCount().getCount().intValue();
    }

    @Override
    public void put(String key, User user) {
        if (user == null || key == null || key.isEmpty()) return;
        repository.insert(user);
    }

    @Override
    public User replace(String key, User user) {
        return repository.update(user, key);
    }

    @Override
    public User remove(String key) {
        User existing = read(key);
        if (existing != null) {
            repository.delete(key);
        }
        return existing;
    }
}
