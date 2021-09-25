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

public interface ReportItemWriter<T> extends ItemWriter<T>, JobExecutionListener {
    Logger getLog();
    String getOutputName();
    String[] getColumnHeaders();
    ContentWriter getWriter();
    default String getSheetName() {return "";}
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
        getLog().info("JobExecutionListener Started!");
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
                getLog().info("JobExecutionListener Completed!");
            } catch (Exception e) {
                getLog().log(Level.WARNING, "JobExecutionListener " + e.getMessage());
            }
        }
    }
}
