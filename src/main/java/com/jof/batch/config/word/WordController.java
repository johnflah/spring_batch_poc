package com.jof.batch.config.word;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;


@RestController
@RequestMapping("/words")
@Slf4j
public class WordController {


    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("importwordsjob")
    private Job importWordsJob;

    @Autowired
    @Qualifier("midjob")
    private Job myJob;



    @PostMapping
    @RequestMapping("/import/{pathparam}")
    public ResponseEntity<?> startImportCSVJob(@RequestParam(name = "message") Optional<String> message, @PathVariable String pathparam){

        try {
            Date date = new Date();
            JobExecution jobExecution = jobLauncher.run(importWordsJob, new JobParametersBuilder()
                            .addDate("launchDate", date)
                            .addString("fullPathFileName","word.txt")
                            .addString("endsWith", message.orElse(""))
                            .toJobParameters());
            System.out.println("Chello");
            return ResponseEntity.ok(jobExecution);
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
