package com.example.springbatch.config.service;

import com.example.springbatch.entity.JobParamsRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("firstJob")
    private Job firstJob;


    @Async
    public void startJob(String jobName, List<JobParamsRequest> jobParams) {
        Map<String, JobParameter<?>> params = new HashMap<>();
        params.put("currentTime", new JobParameter(System.currentTimeMillis(), Long.class));
        jobParams.stream().forEach(jobParam -> params.put(jobParam.getParamKey(), new JobParameter(jobParam.getParamValue(), String.class)));
        JobParameters jobParameters = new JobParameters(params);
        try {

            jobLauncher.run(firstJob, jobParameters);

        } catch (Exception e) {
            System.out.println("Exception occurred at job starting ");
        }
    }
}
