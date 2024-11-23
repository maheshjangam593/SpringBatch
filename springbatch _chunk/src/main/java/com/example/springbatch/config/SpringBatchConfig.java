package com.example.springbatch.config;

import com.example.springbatch.config.service.FirstItemProcessor;
import com.example.springbatch.config.service.FirstItemReader;
import com.example.springbatch.config.service.FirstItemWriter;
import com.example.springbatch.config.service.TaskletService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

//@EnableBatchProcessing
@AllArgsConstructor
@Configuration
public class SpringBatchConfig {

    private TaskletService taskletService;
    private FirstItemReader step1Reader;
    private FirstItemProcessor step1Processor;
    private FirstItemWriter firstItemWriter;

    @Bean
    public Job firstJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("chunk-job", jobRepository).
                flow(step1(jobRepository, transactionManager)).end().build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chunk-step", jobRepository)
                .<Integer, Long>chunk(3, transactionManager)
                .reader(step1Reader)
                .processor(step1Processor)
                .writer(firstItemWriter)
                .build();
    }

    @Bean
    public Job secondJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("taskletJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(secondStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step secondStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("tasklet-step2", jobRepository)
                .tasklet(taskletService, transactionManager).build();
    }


}
