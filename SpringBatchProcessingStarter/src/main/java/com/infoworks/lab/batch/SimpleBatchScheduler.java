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

    /**
     *  (second, minute, hour, day of month, month, day(s) of week)
     *  (* * * * * *)
     *
     *  "0 0 * * * *" = the top of every hour of every day.
     *  "*\/10 * * * * *" = every ten seconds.
     *  "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
     *  "0 0 8,10 * * *" = 8 and 10 o'clock of every day.
     *  "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
     *  "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
     *  "0 0 0 25 12 ?" = every Christmas Day at midnight
     */

    @Scheduled(cron = "${batch.processing.cron.expression}")
    public void process() {
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

}
