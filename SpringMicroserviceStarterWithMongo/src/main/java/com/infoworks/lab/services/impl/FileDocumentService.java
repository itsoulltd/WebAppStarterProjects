package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.FileDocument;
import com.infoworks.lab.domain.repositories.FileDocumentRepository;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.iDocumentService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service("fileDocumentService")
public class FileDocumentService extends SimpleDataSource<String, FileDocument> implements iDocumentService<FileDocument> {

    private static Logger LOG = LoggerFactory.getLogger(FileDocumentService.class.getSimpleName());
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
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }

    @Override
    public List<FileDocument> search(SearchQuery query) {
        Query mQuery = convertIntoMQuery(query);
        //If-Query-Is-Empty: return empty list;
        if (mQuery.getQueryObject().isEmpty()) return new ArrayList<>();
        mQuery.limit(query.getSize());
        List<FileDocument> iterable = template.find(mQuery, FileDocument.class);
        return iterable;
    }

    @Override
    public Map<Long, List<FileDocument>> searchInBatchGroup(SearchQuery query) {
        Map<Long, List<FileDocument>> result = new HashMap<>();
        Query mQuery = convertIntoMQuery(query);
        //
        long maxSize =  template.count(mQuery, FileDocument.class);
        if (maxSize == 0) return result;
        //
        long cursor = (query.getPage() < 0) ? 0 : query.getPage();
        long batchSize = (query.getSize() <= 0) ? 100 : query.getSize();
        FileDocument last = null;
        do{
            //Skip-first-time:
            if (last != null){
                mQuery = convertIntoMQuery(query);
                mQuery.addCriteria(Criteria.where("timestamp").gt(last.getTimestamp()));
            }
            mQuery.limit(query.getSize());
            List<FileDocument> iterable = template.find(mQuery, FileDocument.class);
            last = iterable.get(iterable.size() - 1);
            //
            result.put(cursor, new ArrayList<>(iterable));
            iterable.forEach(fileDocument -> LOG.info("Document: " + fileDocument.getName()));
            LOG.info("-----------------------------------------");
            //
            cursor = cursor + batchSize; //Loop-Increment
        } while (maxSize != -1 && cursor < maxSize);
        return result;
    }

    @Override
    public long remove(SearchQuery query) {
        AtomicLong cursor = new AtomicLong((query.getPage() < 0) ? 0 : query.getPage());
        Map<Long, List<FileDocument>> data = searchInBatchGroup(query);
        data.forEach((key, set) -> {
            cursor.addAndGet(set.size());
            repository.deleteAll(set);
        });
        return cursor.get();
    }

    private Query convertIntoMQuery(SearchQuery query) {
        Query mQuery = new Query();
        //Start with aa -> "^aa"; End with aa -> "aa$"
        query.getProperties().forEach(prop -> {
            if (prop.getKey().equalsIgnoreCase("name")){
                mQuery.addCriteria(Criteria.where("fileMeta.name").regex("^" + prop.getValue()));
            } else if (prop.getKey().equalsIgnoreCase("description")){
                mQuery.addCriteria(Criteria.where("fileMeta.description").regex("^" + prop.getValue()));
            } else if (prop.getKey().equalsIgnoreCase("contentType")){
                mQuery.addCriteria(Criteria.where("fileMeta.contentType").regex("^" + prop.getValue()));
            }
        });
        return mQuery;
    }
}
