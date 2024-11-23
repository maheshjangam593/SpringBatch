package com.example.springbatch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("before job " + jobExecution.getJobInstance().getJobName());
        System.out.println("job parameters " + jobExecution.getJobParameters());
        System.out.println("job execution context " + jobExecution.getExecutionContext());
        jobExecution.getExecutionContext().put("jec", "jec value");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("after job " + jobExecution.getJobInstance().getJobName());
        System.out.println("job parameters " + jobExecution.getJobParameters());
        System.out.println("job execution context " + jobExecution.getExecutionContext());
    }

}
