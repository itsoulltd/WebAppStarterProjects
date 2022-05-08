package com.infoworks.lab.domain.definition;

import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.ExcelWritingService;

import java.util.List;

public interface ExcelItemWriter<T> extends ReportItemWriter<T, List<String>>{

    default ContentWriter createWriter() throws NullPointerException {
        if (getService() == null) throw new NullPointerException("ExcelWritingService must not be null!");
        //Create WRITER:
        ContentWriter writer = createAsyncWriter()
                ? getService().createAsyncWriter(getBatchSize(), getOutputName(), true)
                : getService().createWriter(true, getOutputName(), true);
        return writer;
    }

    int getBatchSize();
    ExcelWritingService getService();
    default boolean createAsyncWriter(){return true;}

}
