package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {
    List<User> findByName(String name);
}
