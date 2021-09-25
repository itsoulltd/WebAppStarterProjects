package com.infoworks.lab.domain.definition;

import com.infoworks.lab.services.ExcelParsingService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ItemWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface ExcelItemWriter<T> extends ItemWriter<T>, JobExecutionListener {

    default ExcelParsingService.AsyncWriter createWriter(ExcelParsingService service) throws NullPointerException{
        if (service == null) throw new NullPointerException("ExcelParsingService must not be null!");
        //Create WRITER:
        ExcelParsingService.AsyncWriter writer = createAsyncWriter()
                ? service.createAsyncWriter(getBatchSize(), getOutputName(), true)
                : service.createWriter(true, getOutputName(), true);
        return writer;
    }
    ExcelParsingService getService();
    ExcelParsingService.AsyncWriter getWriter();
    String getOutputName();
    int getBatchSize();
    Logger getLog();
    default boolean createAsyncWriter(){return false;}
    String[] getColumnHeaders();
    String getSheetName();
    Map<Integer, List<String>> convert(List<? extends T> list);

    @Override
    default void write(List<? extends T> list) throws Exception{
        if (getWriter() == null) return;
        if (list.size() > 0){
            Map results = convert(list);
            if (results.size() > 0 && getWriter() != null){
                //WRITE To Excel:
                getWriter().write(getSheetName(), results, true);
            }
        }
    }

    @Override
    default void beforeJob(JobExecution jobExecution) {
        getLog().info("ExcelItemWriter Started!");
        //WRITE Column Headers:
        if (getWriter() != null){
            if (getColumnHeaders().length > 0){
                Map<Integer, List<String>> results = new HashMap<>();
                results.put(0, Arrays.asList(getColumnHeaders()));
                getWriter().write(getSheetName(), results, false);
            }
        }
    }

    @Override
    default void afterJob(JobExecution jobExecution) {
        //WRITE Commit:
        if (getWriter() != null) {
            try {
                getWriter().close();
                getLog().info("ExcelItemWriter Completed!");
            } catch (Exception e) {
                getLog().log(Level.WARNING, "ExcelItemWriter " + e.getMessage());
            }
        }
    }

}
