package com.example.springbatch.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobExecutionListenerImpl implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Add some data to the execution context
        jobExecution.getExecutionContext().put("someKey", "someValue");
        System.out.println("Execution context before job: " + jobExecution.getExecutionContext());

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Logic after job execution
    }
}

