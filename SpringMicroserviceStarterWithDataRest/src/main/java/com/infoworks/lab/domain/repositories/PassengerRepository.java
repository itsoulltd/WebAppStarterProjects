package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "passengers", path = "passengers")
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    List<Passenger> findByName(String name);
}
