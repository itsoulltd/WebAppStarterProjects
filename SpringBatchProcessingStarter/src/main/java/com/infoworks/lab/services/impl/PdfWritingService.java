package com.infoworks.lab.services.impl;

import com.infoworks.lab.services.definition.WritingService;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PdfWritingService implements WritingService {

    private static Logger LOG = Logger.getLogger(PdfWritingService.class.getSimpleName());

    @Override
    public void write(boolean xssf, OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {

    }

    @Override
    public void writeAsStream(OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {

    }
}
