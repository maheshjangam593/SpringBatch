package com.example.springbatch.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Step Execution started " + stepExecution.getStepName());
        System.out.println("Step Execution started  write count " + stepExecution.getWriteCount());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Get the number of write counts
        int writeCount = (int) stepExecution.getWriteCount();
        System.out.println("Number of records inserted: " + writeCount);
        return stepExecution.getExitStatus();
    }
}
