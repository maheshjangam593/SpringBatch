package com.example.springbatch.config.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecondJobSchedular {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("firstJob")
    private Job firstJob;
    @Autowired
    @Qualifier("secondJob")
    private Job secondJob;

   // @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void startSecondJob() {
        Map<String, JobParameter<?>> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis(), Long.class));
        JobParameters jobParameters = new JobParameters(params);
        try {

            JobExecution jobExecution = jobLauncher.run(secondJob, jobParameters);

            System.out.println("Job execution id in Scheduler "+jobExecution.getId() );
        } catch (Exception e) {
            System.out.println("Exception occurred at job starting ");
        }
    }


}
