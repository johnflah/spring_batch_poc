/*
package com.jof.batch.config;

import com.jof.batch.entity.Customer;
import com.jof.batch.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.Repository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
@EnableBatchProcessing
@RequiredArgsConstructor
@Data
@Slf4j
public class SpringBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CustomerRepository customerRepository;


    private String filename ;



    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerReader(@Value("#{jobParameters['fullPathFileName']}")String fullPathFileName){
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader();

        flatFileItemReader.setResource( new FileSystemResource("src/main/resources/"+fullPathFileName));
        flatFileItemReader.setName("customerCSVReader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }


    @Bean
    public RepositoryItemWriter<Customer> customerRepositoryItemWriter(){

        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");

        return writer;

    }


    @Bean
    public Step stepOne(){
        return stepBuilderFactory.get("csv-step").<Customer,Customer>chunk(10)
                .reader(customerReader(null))
                .processor(processor())
                .writer(customerRepositoryItemWriter())
                .build();

    }


    @Bean
    public Job runJob(){
        return jobBuilderFactory.get("importCustomersInfo").incrementer(new RunIdIncrementer()).flow(stepOne()).end().build();
    }

    private LineMapper<Customer> lineMapper() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer dlt = new DelimitedLineTokenizer();
        dlt.setDelimiter(",");
        dlt.setStrict(false);
        dlt.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");
        BeanWrapperFieldSetMapper<Customer> customerBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        customerBeanWrapperFieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(dlt);
        lineMapper.setFieldSetMapper(customerBeanWrapperFieldSetMapper);

        return lineMapper;
    }


}
*/
