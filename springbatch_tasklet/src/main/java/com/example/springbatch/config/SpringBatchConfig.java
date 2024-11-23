package com.example.springbatch.config;

import com.example.springbatch.entity.Customer;
import com.example.springbatch.listener.FirstJobExecutionListener;
import com.example.springbatch.listener.FirstStepExecutionListener;
import com.example.springbatch.repo.CustomerRepository;
import com.example.springbatch.service.TaskletService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

//@EnableBatchProcessing
@AllArgsConstructor
@Configuration
public class SpringBatchConfig {

    private CustomerRepository customerRepository;
    private TaskletService taskletService;
    private FirstJobExecutionListener firstJobExecutionListener;
    private FirstStepExecutionListener firstStepExecutionListener;


    @Bean
    @StepScope
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new FileSystemResource(pathToFile));
//        itemReader.setName("csvReader");
//        itemReader.setLinesToSkip(1);
//        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    public LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");

        BeanWrapperFieldSetMapper<Customer> wrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        wrapperFieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(wrapperFieldSetMapper);
        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Customer> reader) {
        return new StepBuilder("tasklet-step1", jobRepository)
                .tasklet(taskletService, transactionManager)
                .listener(firstStepExecutionListener).build();
    }

    @Bean
    public Step secondStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("tasklet-step2", jobRepository)
                .tasklet(taskletService, transactionManager).build();
    }



    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Customer> reader) {
        return new JobBuilder("importCustomers", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(firstStep(jobRepository, transactionManager, reader))
                .next(secondStep(jobRepository, transactionManager))
                .listener(firstJobExecutionListener)
                .build();
    }



}
