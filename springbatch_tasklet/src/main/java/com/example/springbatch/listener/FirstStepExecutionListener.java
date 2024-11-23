package com.example.springbatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FirstStepExecutionListener implements StepExecutionListener {
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("before step1 " + stepExecution.getStepName());
        System.out.println("job execution cont " + stepExecution.getJobExecution().getExecutionContext());
        System.out.println("step exe context " + stepExecution.getExecutionContext());
        stepExecution.getExecutionContext().put("sec", "sec value");
    }

    @Nullable
    public ExitStatus afterStep(StepExecution stepExecution) {

        System.out.println("after step1 " + stepExecution.getStepName());
        System.out.println("job execution cont " + stepExecution.getJobExecution().getExecutionContext());
        System.out.println("step exe contexet " + stepExecution.getExecutionContext());
        return null;
    }

}

