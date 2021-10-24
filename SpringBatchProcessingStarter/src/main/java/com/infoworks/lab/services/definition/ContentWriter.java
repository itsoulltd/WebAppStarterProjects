package com.infoworks.lab.services.definition;

import java.util.Map;

public interface ContentWriter<T> extends AutoCloseable{
    default void write(Map<Integer, T> data, boolean skipZeroIndex) { write("", data, skipZeroIndex); }
    void write(String sheetName, Map<Integer, T> data, boolean skipZeroIndex);
}
