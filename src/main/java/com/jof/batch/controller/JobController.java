package com.jof.batch.controller;

import com.jof.batch.config.SpringBatchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@Slf4j
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

    @Autowired
    private SpringBatchConfig springBatchConfig;

    @PostMapping
    @RequestMapping("/importCustomers")
    public void startImportCSVJob(){

        JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).addString("fullPathFileName","customers_copy.csv").toJobParameters();
        springBatchConfig.setFilename("customers_copy.csv");


        try {
            jobLauncher.run(job,jobParameters);
        } catch (Exception e) {
            log.info("Exception : {} "+e.getMessage());
        }

    }

}
