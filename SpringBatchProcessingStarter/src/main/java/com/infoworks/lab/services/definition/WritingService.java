package com.infoworks.lab.services.definition;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface WritingService {
    void write(boolean xssf, OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception;
    void writeAsStream(OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception;
}
