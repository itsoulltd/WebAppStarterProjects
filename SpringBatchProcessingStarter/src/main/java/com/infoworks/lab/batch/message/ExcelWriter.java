package com.infoworks.lab.batch.message;

import com.infoworks.lab.domain.definition.ExcelItemWriter;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.ExcelWritingService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ExcelWriter implements ExcelItemWriter<Message> {

    private static Logger LOG = Logger.getLogger(MessageWriter.class.getSimpleName());
    private String exportPath;
    private ExcelWritingService service;
    private int batchSize = 10;
    private ContentWriter writer;
    private AtomicInteger progressCounter;

    public ExcelWriter(String exportPath, int batchSize, ExcelWritingService service) {
        this.exportPath = exportPath;
        this.service = service;
        this.batchSize = batchSize;
        this.progressCounter = new AtomicInteger(1);
    }

    @Override
    public Logger getLog() {
        return LOG;
    }

    @Override
    public String getOutputName() {
        return exportPath + "sample-excel-" + System.currentTimeMillis() + ".xlsx";
    }

    @Override
    public String[] getColumnHeaders() {
        return new String[]{"Key", "Payload", "Date"};
    }

    @Override
    public ContentWriter getWriter() {
        if (writer == null){
            this.writer = createWriter();
        }
        return writer;
    }

    @Override
    public Map<Integer, List<String>> convert(List<? extends Message> list) {
        //TODO:Dummy
        Map<Integer, List<String>> data = new HashMap<>();
        list.forEach(msg -> {
            String from = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            data.put(progressCounter.getAndIncrement(), Arrays.asList("message", msg.getPayload(), from));
        });
        return data;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public ExcelWritingService getService() {
        return service;
    }

}
