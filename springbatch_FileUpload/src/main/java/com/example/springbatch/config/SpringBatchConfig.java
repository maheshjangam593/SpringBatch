package com.example.springbatch.config;

import com.example.springbatch.entity.Customer;
import com.example.springbatch.listener.StepSkipListener;
import com.example.springbatch.repo.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

//@EnableBatchProcessing
@AllArgsConstructor
@Configuration
public class SpringBatchConfig {

    private CustomerRepository customerRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> reader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile ) {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(pathToFile));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
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
    public Step slaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,FlatFileItemReader<Customer> reader) {
        return new StepBuilder("slave-step", jobRepository).<Customer, Customer>chunk(5, transactionManager).
                reader(reader).processor(processor()).writer(writer())
                .build();
    }


    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,FlatFileItemReader<Customer> reader) {
        return new JobBuilder("importCustomers", jobRepository).
                flow(slaveStep(jobRepository, transactionManager,reader)).end().build();
    }

    public SkipPolicy skipPolicy()
    {
        return new ExceptionSkipPolicy();
    }

    public SkipListener skipListener()
    {
        return new StepSkipListener();
    }
   /* @Bean
    public ColumnRangePartitioner partitioner() {
        return new ColumnRangePartitioner();
    }

    @Bean
    public PartitionHandler partitionHandler(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setGridSize(1);
        partitionHandler.setTaskExecutor(taskExecutor());
        partitionHandler.setStep(slaveStep(jobRepository, transactionManager));
        return partitionHandler;
    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setMaxPoolSize(4);
        threadPoolExecutor.setCorePoolSize(4);
        threadPoolExecutor.setQueueCapacity(4);
        return threadPoolExecutor;
    }*/

   /* @Bean
    public TaskExecutor taskExecutor()
    {
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }*/
/*    @Bean
    public JobRepository jobRepository() throws Exception {
        return new JobRepositoryFactoryBean().getObject();
    }
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }*/
}
