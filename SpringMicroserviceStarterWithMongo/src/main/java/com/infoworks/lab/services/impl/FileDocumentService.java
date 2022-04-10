package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.FileDocument;
import com.infoworks.lab.domain.repositories.FileDocumentRepository;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.iDocumentService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("fileDocumentService")
public class FileDocumentService extends SimpleDataSource<String, FileDocument> implements iDocumentService<FileDocument> {

    private FileDocumentRepository repository;
    private MongoTemplate template;

    public FileDocumentService(FileDocumentRepository repository
                    , MongoTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    @Override
    public FileDocument read(String uuid) {
        Optional<FileDocument> doc = repository.findByUuid(uuid);
        return doc.isPresent() ? doc.get() : null;
    }

    @Override
    public FileDocument[] readSync(int offset, int pageSize) {
        Page<FileDocument> page = repository.findAll(PageRequest.of(offset, pageSize));
        return page.getContent().toArray(new FileDocument[0]);
    }

    @Override
    public void put(String uuid, FileDocument fileDocument) {
        add(fileDocument);
    }

    @Override
    public void add(FileDocument fileDocument) {
        if (fileDocument == null) return;
        if (fileDocument.getUuid() == null || fileDocument.getUuid().isEmpty()) return;
        repository.save(fileDocument);
    }

    @Override
    public FileDocument replace(String uuid, FileDocument fileDocument) {
        if (fileDocument == null) return null;
        FileDocument existing = read(uuid);
        if (existing != null){
            fileDocument.setUuid(existing.getUuid());
            existing.unmarshallingFromMap(fileDocument.marshallingToMap(true), true);
            repository.save(existing);
        }
        return existing;
    }

    @Override
    public FileDocument remove(String uuid) {
        FileDocument existing = read(uuid);
        if (existing != null){
            repository.delete(existing);
        }
        return existing;
    }

    @Override
    public FileDocument findByName(String name) {
        //Using Template
        FileDocument document = template.findOne(Query.query(Criteria.where("fileMeta.name").is(name)), FileDocument.class);
        return document;
    }

    @Override
    public List<FileDocument> search(SearchQuery query) {
        String search = query.get("search", String.class);
        Query mQuery = new Query();
        mQuery.limit(query.getSize());
        //Start with aa -> "^aa"; End with aa -> "aa$"
        mQuery.addCriteria(Criteria.where("fileMeta.name").regex("^" + search));
        List<FileDocument> iterable = template.find(mQuery, FileDocument.class);
        return iterable;
    }

    @Override
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }
}
