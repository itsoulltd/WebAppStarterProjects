package com.infoworks.lab.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SimpleBatchScheduler {

    private JobLauncher jobLauncher;
    private Job job;

    public SimpleBatchScheduler(JobLauncher jobLauncher
            , @Qualifier("simpleJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @Value("${batch.processing.cron.prevent.execution}")
    private boolean shouldPreventExecution;

    @Async
    @Scheduled(cron = "${batch.processing.cron.expression}")
    public void process() {
        //System.out.println("Process: " + Thread.currentThread().getName());
        if (shouldPreventExecution){
            System.out.println("ShouldPreventExecution: YES");
            return;
        }
        //
        System.out.println("Running");
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        try {
            jobLauncher.run(job, params);
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

    /*@Async
    @Scheduled(cron = "${batch.processing.cron.expression}")
    public void testOne(){
        System.out.println("One: " + Thread.currentThread().getName());
        for (int i = 0; i < 100; i++){
            System.out.println("One: " + i);
        }
    }*/

    /*@Async
    @Scheduled(cron = "${batch.processing.cron.expression}")
    public void testTwo(){
        System.out.println("Two: " + Thread.currentThread().getName());
        for (int i = 0; i < 100; i++){
            System.out.println("Two: " + i);
        }
    }*/

}
