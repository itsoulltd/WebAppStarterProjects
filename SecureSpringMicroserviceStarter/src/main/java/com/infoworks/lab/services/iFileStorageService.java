package com.infoworks.lab.services;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface iFileStorageService<IOStream> extends DataSource<String, IOStream> {
    boolean save(String location, IOStream file) throws IOException;
    default void retry(boolean async) {}
    String[] fileNames();
    InputStream findByName(String name);
    List<InputStream> search(SearchQuery query);
}
