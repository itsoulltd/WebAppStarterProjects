package com.infoworks.lab.domain.definition;

import com.infoworks.lab.services.definition.ContentWriter;
import com.infoworks.lab.services.impl.PdfWritingService;

public interface PdfItemWriter<T> extends ReportItemWriter<T> {

    PdfWritingService getService();
    default ContentWriter createWriter(PdfWritingService service) throws NullPointerException {
        if (service == null) throw new NullPointerException("PdfWritingService must not be null!");
        //Create WRITER:
        ContentWriter writer = service.createWriter(getOutputName(), true);
        return writer;
    }

}
