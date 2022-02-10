package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.util.List;

public interface iDocumentService<File> extends DataSource<String, File> {
    File findByName(String name);
    List<File> search(SearchQuery query);
}
