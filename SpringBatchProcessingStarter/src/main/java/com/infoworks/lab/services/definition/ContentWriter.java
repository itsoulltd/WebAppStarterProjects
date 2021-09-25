package com.infoworks.lab.services.definition;

import java.util.List;
import java.util.Map;

public interface ContentWriter extends AutoCloseable{
    default void write(Map<Integer, List<String>> data, boolean skipZeroIndex) { write("", data, skipZeroIndex); }
    void write(String sheetName, Map<Integer, List<String>> data, boolean skipZeroIndex);
}
