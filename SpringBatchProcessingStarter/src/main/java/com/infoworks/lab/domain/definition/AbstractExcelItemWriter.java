package com.infoworks.lab.domain.definition;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.ExcelWritingService;
import org.springframework.batch.core.JobExecution;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractExcelItemWriter<M extends Message> implements ExcelItemWriter<M> {

    private String exportPath;
    private ExcelWritingService service;
    private int batchSize = 10;
    private ContentWriter writer;
    private AtomicInteger excelRowCounter;

    public AbstractExcelItemWriter(ExcelWritingService service, String exportPath) {
        this.exportPath = exportPath;
        this.service = service;
        this.excelRowCounter = new AtomicInteger(1);
    }

    public AbstractExcelItemWriter(ExcelWritingService service, String exportPath, int batchSize) {
        this(service, exportPath);
        this.batchSize = batchSize;
    }

    public int getNextRowIndex(){
        return excelRowCounter.getAndIncrement();
    }

    public String getExportPath() {
        return exportPath;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public ExcelWritingService getService() {
        return service;
    }

    @Override
    public ContentWriter getWriter() {
        if (writer == null){
            this.writer = createWriter();
        }
        return writer;
    }

    @Override
    public void afterJobCleanup(JobExecution jobExecution) {
        try {
            getWriter().close();
        } catch (Exception e) {}
        this.writer = null;
    }
}
