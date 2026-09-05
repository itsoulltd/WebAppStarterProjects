package com.infoworks.domain.repositories;

import com.infoworks.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, SearchableRepository<User, Integer> {
    List<User> findByName(String name);

    @Query("SELECT u FROM tbl_user u WHERE u.email = :query OR u.name = :query")
    List<User> findByNameOrEmail(@Param("query") String query);
}
