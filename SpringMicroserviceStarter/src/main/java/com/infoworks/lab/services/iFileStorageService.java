package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface iFileStorageService<IOStream> extends DataSource<String, IOStream> {
    boolean save(String location, IOStream file) throws IOException;
    default void retry(boolean async) {}
    String[] fileNames();
    InputStream findByName(String name);
    /**
     * TO Search files (containing file-name), pass SearchQuery.add("filename").isEqualTo("abc");
     * where filename should be the key.
     * TO Search files in Directory (containing directory-name), pass SearchQuery.add("dirname").isEqualTo("abc");
     * where dirname should be the key.
     * @param query
     * @return List<InputStream>
     */
    List<InputStream> search(SearchQuery query);
    List<File> searchFiles(File searchDir, SearchQuery query);
    void prepareZipEntryFrom(List<File> files, OutputStream oStream) throws IOException;
}
