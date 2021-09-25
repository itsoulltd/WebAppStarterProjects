package com.infoworks.lab.domain.definition;

import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.ExcelParsingService;

public interface ExcelItemWriter<T> extends ReportItemWriter<T>{

    default ContentWriter createWriter(ExcelParsingService service) throws NullPointerException {
        if (service == null) throw new NullPointerException("ExcelParsingService must not be null!");
        //Create WRITER:
        ContentWriter writer = createAsyncWriter()
                ? service.createAsyncWriter(getBatchSize(), getOutputName(), true)
                : service.createWriter(true, getOutputName(), true);
        return writer;
    }

    int getBatchSize();
    ExcelParsingService getService();
    default boolean createAsyncWriter(){return false;}

}
