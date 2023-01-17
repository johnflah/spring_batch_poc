package com.jof.batch.config;


import com.jof.batch.config.word.WordRepository;
import com.jof.batch.entity.MerchantEnablementStatus;
import com.jof.batch.repository.MerchantEnablementStatusRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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

@Configuration
@RequiredArgsConstructor
@Data
@Slf4j
public class MidBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MerchantEnablementStatusRepository merchantEnablementStatusRepository;
    private final WordRepository wordRepository;


    private String filename;

    @Bean
    @StepScope
    public FlatFileItemReader<MerchantEnablementStatus> midReader(@Value("#{jobParameters['fullPathFileName']}") String fullPathFileName) {

        FlatFileItemReader<MerchantEnablementStatus> flatFileItemReader = new FlatFileItemReader();

        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/" + fullPathFileName));
        flatFileItemReader.setName("MerchantEnablementStatusCSVReader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    private LineMapper<MerchantEnablementStatus> lineMapper() {

        DefaultLineMapper<MerchantEnablementStatus> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer dlt = new DelimitedLineTokenizer();
        dlt.setDelimiter(",");
        dlt.setStrict(false);
        dlt.setNames("mid");
        BeanWrapperFieldSetMapper<MerchantEnablementStatus> merchantEnablementStatusBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        merchantEnablementStatusBeanWrapperFieldSetMapper.setTargetType(MerchantEnablementStatus.class);

        lineMapper.setLineTokenizer(dlt);
        lineMapper.setFieldSetMapper(merchantEnablementStatusBeanWrapperFieldSetMapper);

        return lineMapper;
    }

    @Bean
    public Step importMidStep() {
        return stepBuilderFactory.get("csv-step").<MerchantEnablementStatus, MerchantEnablementStatus>chunk(10)
                .reader(midReader(null))
                .processor(midProcessor())
                .writer(merchantEnablementStatusRepositoryItemWriter())
                .build();

    }





    @Bean
    public MidProcessor midProcessor() {
        return new MidProcessor();
    }



    @Bean
    public RepositoryItemWriter<MerchantEnablementStatus> merchantEnablementStatusRepositoryItemWriter(){

        RepositoryItemWriter<MerchantEnablementStatus> writer = new RepositoryItemWriter<>();
        writer.setRepository(merchantEnablementStatusRepository);
        writer.setMethodName("save");

        return writer;

    }


    @Bean("midjob")
    public Job runJob(Step endsWithStep){

        return jobBuilderFactory.get("importMerchants").incrementer(new RunIdIncrementer())
                .start(importMidStep()).next(endsWithStep).build();
    }


}
