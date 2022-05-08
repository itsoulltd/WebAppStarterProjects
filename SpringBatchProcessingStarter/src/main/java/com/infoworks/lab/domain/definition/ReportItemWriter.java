package com.infoworks.lab.domain.definition;

import com.infoworks.lab.services.definition.ContentWriter;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ItemWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface ReportItemWriter<T, S> extends ItemWriter<T>, JobExecutionListener {
    Logger getLog();
    String getOutputName();
    String[] getColumnHeaders();
    ContentWriter getWriter();
    default String getSheetName() {return "default";}
    Map<Integer, S> convert(List<? extends T> list);

    @Override
    default void write(List<? extends T> list) throws Exception{
        if (list.size() <= 0) return;;
        if (getWriter() == null) return;
        //
        Map results = convert(list);
        if (results != null && results.size() > 0){
            //WRITE To Source:
            getWriter().write(getSheetName(), results, true);
        }
    }

    @Override
    default void beforeJob(JobExecution jobExecution) {
        getLog().info("JobExecutionListener Started!");
        //WRITE Column Headers:
        if (getWriter() != null){
            if (getColumnHeaders() != null && getColumnHeaders().length > 0){
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
                getLog().info("JobExecutionListener Completed!");
            } catch (Exception e) {
                getLog().log(Level.WARNING, "JobExecutionListener " + e.getMessage());
            }
        }
    }
}
