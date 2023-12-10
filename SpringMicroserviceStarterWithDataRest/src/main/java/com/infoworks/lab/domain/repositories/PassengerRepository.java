package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "passengers", path = "passengers")
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    List<Passenger> findByName(String name);

    @Query(value = "SELECT * FROM Passenger p WHERE p.age >= :min and p.age <= :max", nativeQuery = true)
    List<Passenger> findByAgeLimit(@Param("min") Integer min, @Param("max") Integer max);
}
