package com.example.springbatch.config;

import com.example.springbatch.config.service.FirstItemProcessor;
import com.example.springbatch.config.service.FirstItemReader;
import com.example.springbatch.config.service.FirstItemWriter;
import com.example.springbatch.config.service.TaskletService;
import com.example.springbatch.entity.StudentCsv;
import com.example.springbatch.entity.StudentJdbc;
import com.example.springbatch.entity.StudentJson;
import com.example.springbatch.entity.StudentXml;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

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
        JobParameters jobParameters = new JobParametersBuilder().addLong("starAt", System.currentTimeMillis()).toJobParameters();
        return new JobBuilder("reader-job", jobRepository).
                flow(step1(jobRepository, transactionManager)).end().build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("reader-step", jobRepository)
                .<StudentJdbc, StudentJdbc>chunk(3, transactionManager)
                //.reader(flatFileItemReader(null))
                //.reader(jsonItemReader(null))
                //.reader(staxEventItemReader(null))
                .reader(jdbcCursorItemReader())
                //.processor(firstItemProcessor)
                .writer(firstItemWriter)
                .build();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.universitydatasource")
    public DataSource universitydatasource() {
        return DataSourceBuilder.create().build();
    }


    @StepScope
    @Bean
    public FlatFileItemReader<StudentCsv> flatFileItemReader(
            @Value("#{jobParameters['inputFilePath']}") FileSystemResource fileSystemResource) {
        FlatFileItemReader<StudentCsv> flatFileItemReader =
                new FlatFileItemReader<StudentCsv>();

        flatFileItemReader.setResource(fileSystemResource);

        flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("ID", "First Name", "Last Name", "Email");
                    }
                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
                    {
                        setTargetType(StudentCsv.class);
                    }
                });

            }
        });

		/*
		DefaultLineMapper<StudentCsv> defaultLineMapper =
				new DefaultLineMapper<StudentCsv>();

		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

		BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper =
				new BeanWrapperFieldSetMapper<StudentCsv>();
		fieldSetMapper.setTargetType(StudentCsv.class);

		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		flatFileItemReader.setLineMapper(defaultLineMapper);
		*/

        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }

    @StepScope
    @Bean
    public JsonItemReader<StudentJson> jsonItemReader(
            @Value("#{jobParameters['inputFilePath']}") FileSystemResource fileSystemResource) {
        JsonItemReader<StudentJson> jsonItemReader =
                new JsonItemReader<StudentJson>();

        jsonItemReader.setResource(fileSystemResource);
        jsonItemReader.setJsonObjectReader(
                new JacksonJsonObjectReader<>(StudentJson.class));

        jsonItemReader.setMaxItemCount(8);
        jsonItemReader.setCurrentItemCount(2);

        return jsonItemReader;
    }

    @StepScope
    @Bean
    public StaxEventItemReader<StudentXml> staxEventItemReader(
            @Value("#{jobParameters['inputFilePath']}") FileSystemResource fileSystemResource) {
        StaxEventItemReader<StudentXml> staxEventItemReader =
                new StaxEventItemReader<StudentXml>();

        staxEventItemReader.setResource(fileSystemResource);
        staxEventItemReader.setFragmentRootElementName("student");
        staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXml.class);
            }
        });

        return staxEventItemReader;
    }

    public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
        JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader =
                new JdbcCursorItemReader<StudentJdbc>();

        jdbcCursorItemReader.setDataSource(universitydatasource());
        jdbcCursorItemReader.setSql(
                "select id, address as address, name as Name "
                        + " from user");

        jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
            {
                setMappedClass(StudentJdbc.class);
            }
        });

        //jdbcCursorItemReader.setCurrentItemCount(2);
        //jdbcCursorItemReader.setMaxItemCount(8);

        return jdbcCursorItemReader;
    }


}
