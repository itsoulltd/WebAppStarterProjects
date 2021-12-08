package com.infoworks.lab.domain.repositories;

import com.infoworks.lab.domain.entities.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {
    Optional<FileDocument> findByUuid(String uuid);
}
