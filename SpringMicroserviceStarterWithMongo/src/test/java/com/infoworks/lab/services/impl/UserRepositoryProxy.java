package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.domain.repositories.UserRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserRepositoryProxy extends SimpleMongoRepository<User, Integer> implements UserRepository {

    private MongoOperations mongoOperations;

    public UserRepositoryProxy(MongoEntityInformation<User, Integer> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<User> findByName(String name) {
        List<User> result = mongoOperations.find(
                Query.query(Criteria.where("name").is(name))
                , User.class);
        return result;
    }
}
