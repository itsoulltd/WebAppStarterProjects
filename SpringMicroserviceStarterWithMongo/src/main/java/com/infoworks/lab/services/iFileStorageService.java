package com.infoworks.lab.services;

import java.io.IOException;

public interface iFileStorageService<IOStream> extends iDocumentService<IOStream> {
    boolean save(String location, IOStream file) throws IOException;
    default void retry(boolean async) {}
    String[] fileNames();
}
