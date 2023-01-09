package com.jof.batch.controller;

import com.jof.batch.config.MidBatchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping("/mids")
@Slf4j
public class MidController {


    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Autowired
    MidBatchConfig midBatchConfig;


    @PostMapping
    @RequestMapping("/importMerchants")
    public void startImportCSVJob(){

        try {
            Date date = new Date();
            JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder()
                            .addDate("launchDate", date)
                            .addString("fullPathFileName","mids.csv")
                            .toJobParameters());

            System.out.println("Chello");
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }

}
