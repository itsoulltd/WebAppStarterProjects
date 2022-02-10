package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.GridFileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GridFileDocumentRepository extends MongoRepository<GridFileDocument, String> {
    Optional<GridFileDocument> findByUuid(String uuid);
    Optional<GridFileDocument> findByName(String name);
}
