package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface iDocumentService<File> extends DataSource<String, File> {
    File findByName(String name);
    /**
     * TO Search files (containing file-name), pass SearchQuery.add("filename").isEqualTo("abc");
     * where filename should be the key.
     * TO Search files in Directory (containing directory-name), pass SearchQuery.add("dirname").isEqualTo("abc");
     * where dirname should be the key.
     * @param query
     * @return List<InputStream>
     */
    List<File> search(SearchQuery query);
    default void search(SearchQuery query, BiConsumer<Long, List<File>> consumer) {
        if (consumer != null) consumer.accept(0l, search(query));
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
