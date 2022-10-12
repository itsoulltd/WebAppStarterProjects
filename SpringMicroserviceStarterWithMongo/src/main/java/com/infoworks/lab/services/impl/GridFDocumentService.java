package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.GridFileDocument;
import com.infoworks.lab.domain.repositories.GridFileDocumentRepository;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.iDocumentService;
import com.it.soul.lab.data.simple.SimpleDataSource;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service("gridFDocumentService")
public class GridFDocumentService extends SimpleDataSource<String, GridFileDocument> implements iDocumentService<GridFileDocument> {

    private static Logger LOG = LoggerFactory.getLogger(GridFDocumentService.class);
    private GridFileDocumentRepository repository;
    private GridFsTemplate template;

    public GridFDocumentService(GridFileDocumentRepository repository
            , GridFsTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    @Override
    public GridFileDocument read(String uuid) {
        Optional<GridFileDocument> doc = repository.findByUuid(uuid);
        if(!doc.isPresent()) return null;
        try {
            GridFileDocument gfDoc = doc.get();
            GridFSFile document = template.findOne(Query.query(Criteria.where("filename").is(gfDoc.getName())));
            GridFsResource gr = template.getResource(document);
            gfDoc.setContent(gr.getInputStream());
            return gfDoc;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public GridFileDocument[] readSync(int offset, int pageSize) {
        Page<GridFileDocument> page = repository.findAll(PageRequest.of(offset, pageSize));
        return page.getContent().toArray(new GridFileDocument[0]);
    }

    @Override
    public void put(String uuid, GridFileDocument saved) {
        add(saved);
    }

    @Override
    public String add(GridFileDocument saved) {
        if (saved == null) return null;
        try {
            ObjectId _id = template.store(saved.getContent(), saved.getName(), saved.getContentType());
            saved.setUuid(_id.toString());
            return repository.save(saved).getUuid();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public GridFileDocument replace(String uuid, GridFileDocument fileDocument) {
        if (fileDocument == null) return null;
        Optional<GridFileDocument> existing = repository.findByUuid(uuid);
        if (existing.isPresent()){
            try {
                //Going to replaced:
                template.delete(Query.query(Criteria.where("_id").is(uuid)));
                ObjectId _id = template.store(existing.get().getContent(), existing.get().getName(), existing.get().getContentType());
                //Update in meta-data:
                fileDocument.setUuid(_id.toString());
                existing.get().unmarshallingFromMap(fileDocument.marshallingToMap(true), true);
                GridFileDocument replaced = repository.save(existing.get());
                return replaced;
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public GridFileDocument remove(String uuid) {
        Optional<GridFileDocument> existing = repository.findByUuid(uuid);
        if (existing.isPresent()){
            try {
                template.delete(Query.query(Criteria.where("_id").is(uuid)));
                repository.delete(existing.get());
                return existing.get();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public GridFileDocument findByName(String name) {
        //Using Template
        Optional<GridFileDocument> doc = repository.findByName(name);
        if(!doc.isPresent()) return null;
        GridFileDocument gfDoc = doc.get();
        return gfDoc;
    }

    @Override
    public List<GridFileDocument> search(SearchQuery query) {
        //TODO:Using Template
        return null;
    }

    @Override
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }
}
