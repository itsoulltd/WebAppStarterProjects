package com.infoworks.lab.batch.message;

import com.infoworks.lab.domain.definition.AbstractExcelItemWriter;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.services.excel.ExcelWritingService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class ExcelWriter extends AbstractExcelItemWriter<Message> {

    private static Logger LOG = Logger.getLogger(ExcelWriter.class.getSimpleName());

    public ExcelWriter(ExcelWritingService service, String exportPath, int batchSize) {
        super(service, exportPath, batchSize);
    }

    @Override
    public Logger getLog() {
        return LOG;
    }

    @Override
    public String getOutputName() {
        return getExportPath() + "sample-excel-" + System.currentTimeMillis() + ".xlsx";
    }

    @Override
    public String[] getColumnHeaders() {
        return new String[]{"Key", "Payload", "Date"};
    }

    @Override
    public Map<Integer, List<String>> convert(List<? extends Message> list) {
        //TODO:Dummy
        System.out.println("ExcelWriter " + Thread.currentThread().getName());
        Map<Integer, List<String>> data = new HashMap<>();
        list.forEach(msg -> {
            String from = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            data.put(getNextRowIndex(), Arrays.asList("message", msg.getPayload(), from));
        });
        return data;
    }

}
