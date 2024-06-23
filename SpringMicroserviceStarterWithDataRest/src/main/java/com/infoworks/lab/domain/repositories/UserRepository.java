package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByName(String name);

    @Query(value = "SELECT * FROM User p WHERE p.age >= :min and p.age <= :max", nativeQuery = true)
    List<User> findByAgeLimit(@Param("min") Integer min, @Param("max") Integer max);
}
