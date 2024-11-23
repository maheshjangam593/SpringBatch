package com.example.springbatch.controller;


import com.example.springbatch.config.service.JobService;
import com.example.springbatch.entity.JobParamsRequest;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobController {


    @Autowired
    private JobOperator jobOperator;

    @Autowired
    JobService jobService;

    @GetMapping("/start/{jobName}")
    public ResponseEntity<String> runJob(@PathVariable("jobName") String jobName, @RequestBody List<JobParamsRequest> jobParams) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        //JobParameters jobParameters = new JobParametersBuilder().addLong("starAt", System.currentTimeMillis()).toJobParameters();


        jobService.startJob(jobName, jobParams);
        return ResponseEntity.ok("Job started " + jobName);
    }

    @GetMapping("/stop/{jobExecutionId}")
    public ResponseEntity<String> stopJob(@PathVariable("jobExecutionId") int jobExecutionId) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        //JobParameters jobParameters = new JobParametersBuilder().addLong("starAt", System.currentTimeMillis()).toJobParameters();

        try {
            jobOperator.stop(jobExecutionId);
        } catch (Exception e) {
            System.out.println("error occurred ");
        }

        return ResponseEntity.ok("Job stopped for job execution Id " + jobExecutionId);
    }

     /*  @PostMapping("/importCustomers")
    public void inputCSVJob() {
        JobParameters jobParameters = new JobParametersBuilder().addLong("starAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }*/

}
