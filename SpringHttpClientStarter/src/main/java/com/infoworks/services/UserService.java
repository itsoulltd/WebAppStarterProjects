package com.infoworks.services;

import com.infoworks.domain.entities.User;
import com.infoworks.domain.repositories.UserRepository;
import com.infoworks.data.impl.SimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserService extends SimpleDataSource<String, User> {

    private static Logger LOG = LoggerFactory.getLogger(UserService.class);
    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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
        String savedId = add(user);
        LOG.info("New User Created: " + savedId);
    }

    @Override
    public String add(User user) throws RuntimeException {
        if (read(user.getName()) != null)
            throw new RuntimeException("User already exist by this name!");
        //Other-wise:
        User saved = repository.save(user);
        return saved.getName();
    }

    @Override
    public User replace(String key, User user) {
        User existing = read(key);
        if (existing != null && user != null) {
            user.setId(existing.getId());
            existing.unmarshalling(user.marshalling(true), true);
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

    public List<User> search(String query) {
        return repository.findByNameOrEmail(query);
    }
}
