package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.util.*;
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
    static boolean isValidBase64String(String base64) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            decoder.decode(base64.getBytes());
            return true;
        } catch (Exception e) {}
        return false;
    }
}
