package com.jof.batch.config.word;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class WordListener implements JobExecutionListener {


    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("in the beginning");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info("in the ned ");

    }
}
