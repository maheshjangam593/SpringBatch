package com.example.springbatch.config.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskletService implements Tasklet {
    Logger logger = LoggerFactory.getLogger(TaskletService.class);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("step name" + chunkContext.getStepContext().getStepName());
        System.out.println("context in tasklet " + chunkContext.getStepContext().getJobExecutionContext());
        return RepeatStatus.FINISHED;
    }
}
