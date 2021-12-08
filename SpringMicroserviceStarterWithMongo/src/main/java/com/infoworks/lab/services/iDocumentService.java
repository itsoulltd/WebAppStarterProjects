package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.FileDocument;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.util.List;

public interface iDocumentService extends DataSource<String, FileDocument> {
    FileDocument findByName(String name);
    List<FileDocument> search(SearchQuery query);
}
