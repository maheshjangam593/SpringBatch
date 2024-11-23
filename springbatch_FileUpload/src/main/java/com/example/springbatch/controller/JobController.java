package com.example.springbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    private final String TEMP_STORAGE="D:/JAVA/SpringBatch/";

    @PostMapping("/importCustomers")
    public void inputCSVJob(@RequestParam("file")MultipartFile multipartFile) throws IOException {
        String originalFileName=multipartFile.getOriginalFilename();
        File fileToImport=new File(TEMP_STORAGE+originalFileName);
        multipartFile.transferTo(fileToImport);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fullPathFileName",TEMP_STORAGE+originalFileName)
        .addLong("starAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

}
