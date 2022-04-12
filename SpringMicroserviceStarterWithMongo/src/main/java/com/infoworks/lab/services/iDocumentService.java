package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface iDocumentService<File> extends DataSource<String, File> {
    File findByName(String name);
    List<File> search(SearchQuery query);
    default void search(SearchQuery query, BiConsumer<Long, List<File>> consumer) {
        if (consumer != null) consumer.accept(0l, new ArrayList<>());
    }
    default Map<Long, List<File>> searchInBatchGroup(SearchQuery query) {return new HashMap<>();}
    default long remove(SearchQuery query) {return 0l;}
}
