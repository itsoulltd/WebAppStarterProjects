package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.WebsiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface WebsiteUserRepository extends JpaRepository<WebsiteUser, Long> {
    List<WebsiteUser> findByName(@Param("name") String name);
}
