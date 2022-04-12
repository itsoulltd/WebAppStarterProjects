package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface iDocumentService<File> extends DataSource<String, File> {
    File findByName(String name);
    List<File> search(SearchQuery query);
    default Map<Long, List<File>> searchInBatchGroup(SearchQuery query) {return new HashMap<>();}
    default long remove(SearchQuery query) {return 0l;}
}
