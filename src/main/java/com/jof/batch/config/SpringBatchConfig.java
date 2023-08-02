package com.jof.batch.config;

import com.jof.batch.entity.Customer;
import com.jof.batch.entity.DummyCustomer;
import com.jof.batch.processor.CustomerCapitalCaseProcessor;
import com.jof.batch.processor.CustomerNameChangeProcessor;
import com.jof.batch.processor.DummyCustomerChangeProcessor;
import com.jof.batch.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
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
    private final CustomerCapitalCaseProcessor customerCapitalCaseProcessor;
    private final CustomerNameChangeProcessor customerNameChangeProcessor;
    private final DummyCustomerChangeProcessor dummyCustomerChangeProcessor;

    private String filename;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerReader(
            @Value("#{jobParameters['fullPathFileName']}") String fullPathFileName) {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader();

        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/" + fullPathFileName));
        flatFileItemReader.setName("customerCSVReader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> customerRepositoryItemWriter() {

        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");

        return writer;

    }

    @Bean
    public FlatFileItemWriter<DummyCustomer> dummyCustomerRepositoryItemWriter() {

        FlatFileItemWriter<DummyCustomer> dummyWriter = new FlatFileItemWriter();

        dummyWriter.setResource(new FileSystemResource("/tmp/test.csv"));
        dummyWriter.setLineAggregator(getDelimitedLineAggregator());

        return dummyWriter;

    }

    private DelimitedLineAggregator<DummyCustomer> getDelimitedLineAggregator() {
        BeanWrapperFieldExtractor<DummyCustomer> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<DummyCustomer>();
        beanWrapperFieldExtractor.setNames(new String[]{"name"});

        DelimitedLineAggregator<DummyCustomer> aggregator = new DelimitedLineAggregator<DummyCustomer>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(beanWrapperFieldExtractor);
        return aggregator;

    }


    @Bean
    public Step stepOne() {
        return stepBuilderFactory.get("csv-step").<Customer, Customer>chunk(10)
                .reader(customerReader(null))
                .processor(processor())
                .writer(customerRepositoryItemWriter())
                .build();

    }

    @Bean
    public Step compositeStep() throws Exception {
        return stepBuilderFactory.get("csv-step").<Customer, Customer>chunk(10)
                .reader(customerReader(null))
                .processor(compositeItemProcessor())
                .writer(customerRepositoryItemWriter())
                .build();

    }

    @Bean
    public Step compositeDummyCustomerStep() throws Exception {
        return stepBuilderFactory.get("composite-dummy-customer-step").<Customer, DummyCustomer>chunk(10)
                .reader(customerReader(null))
                .processor(compositeDummyCustomerItemProcessor())
                .writer(dummyCustomerRepositoryItemWriter())
                .build();

    }

    @Bean
    public Job runJob() throws Exception {
        return jobBuilderFactory.get("importCustomersInfo").incrementer(new RunIdIncrementer())
                .start(compositeStep())
                .next(compositeDummyCustomerStep())
                // .flow(stepOne()).end()
                .build();
    }

    @Bean
    public CompositeItemProcessor<Customer, Customer> compositeItemProcessor() throws Exception {
        CompositeItemProcessor<Customer, Customer> processor = new CompositeItemProcessor<>();

        List<ItemProcessor<Customer, Customer>> processors = new ArrayList<>();

        processors.add(customerNameChangeProcessor);
        processors.add(customerCapitalCaseProcessor);
        processor.setDelegates(processors);
        processor.afterPropertiesSet();

        return processor;
    }

    @Bean
    public CompositeItemProcessor<Customer, DummyCustomer> compositeDummyCustomerItemProcessor() throws Exception {
        CompositeItemProcessor<Customer, DummyCustomer> processor = new CompositeItemProcessor<>();

        List<ItemProcessor<Customer, DummyCustomer>> processors = new ArrayList<>();

        // processors.add();
        processors.add(dummyCustomerChangeProcessor);
        processor.setDelegates(processors);
        processor.afterPropertiesSet();

        return processor;
    }

    private LineMapper<Customer> lineMapper() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer dlt = new DelimitedLineTokenizer();
        dlt.setDelimiter(",");
        dlt.setStrict(false);
        dlt.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
        BeanWrapperFieldSetMapper<Customer> customerBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        customerBeanWrapperFieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(dlt);
        lineMapper.setFieldSetMapper(customerBeanWrapperFieldSetMapper);

        return lineMapper;
    }

}
