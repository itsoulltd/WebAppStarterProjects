package com.infoworks.lab.domain.definition;

import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.ExcelWritingService;

public interface ExcelItemWriter<T> extends ReportItemWriter<T>{

    default ContentWriter createWriter(ExcelWritingService service) throws NullPointerException {
        if (service == null) throw new NullPointerException("ExcelWritingService must not be null!");
        //Create WRITER:
        ContentWriter writer = createAsyncWriter()
                ? service.createAsyncWriter(getBatchSize(), getOutputName(), true)
                : service.createWriter(true, getOutputName(), true);
        return writer;
    }

    int getBatchSize();
    ExcelWritingService getService();
    default boolean createAsyncWriter(){return false;}

}
