package com.example.springbatch.config;

import com.example.springbatch.config.partitioner.ColumnRangePartitioner;
import com.example.springbatch.entity.Customer;
import com.example.springbatch.repo.CustomerRepository;
import lombok.AllArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.concurrent.ThreadPoolExecutor;

//@EnableBatchProcessing
@AllArgsConstructor
@Configuration
public class SpringBatchConfig {

    private CustomerRepository customerRepository;
    private CustomerWriter customerWriter;

    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
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
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

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

/*    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }*/

    @Bean
    public Step slaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("slave-step", jobRepository).<Customer, Customer>chunk(1, transactionManager).
                reader(reader()).processor(processor()).writer(customerWriter)
                .listener(new CustomStepListener()).listener(new CustomChunkListener())

                .build();
    }


    @Bean
    public Step masterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("master-step", jobRepository).partitioner(slaveStep(jobRepository, transactionManager).getName(), partitioner())
                .partitionHandler(partitionHandler(jobRepository, transactionManager)).build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importCustomers", jobRepository).
                flow(masterStep(jobRepository, transactionManager)).end().build();
    }

    @Bean
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
    }

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
