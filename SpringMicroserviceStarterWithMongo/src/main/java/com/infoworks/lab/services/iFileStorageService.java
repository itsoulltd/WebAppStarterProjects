package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface iFileStorageService<IOStream> extends iDocumentService<IOStream> {
    boolean save(String location, IOStream file) throws IOException;
    default void retry(boolean async) {}
    String[] fileNames();
    List<File> searchFiles(File searchDir, SearchQuery query);
}
