package com.jof.batch.config.word;

import com.jof.batch.config.word.processor.FlippedEndswithProcessor;
import com.jof.batch.entity.Word;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;

import java.util.*;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class WordBatchConfig extends DefaultBatchConfigurer {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final WordRepository wordRepository;

    @Override
    public JobLauncher getJobLauncher() {

        try {
            SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(getJobRepository());
            jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
            jobLauncher.afterPropertiesSet();
            return jobLauncher;
        } catch (Exception e) {
            log.info("Exception {} : ", e.getMessage());
            return super.getJobLauncher();
        }

    }

    @Bean("importwordsjob")
    public Job importWords(Step readWordsFromFileAndChangeCaseStep, Step readWordsBackInAndReverseStep, Step endsWithStep ){
        return jobBuilderFactory.get("importWords")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
//                .start(endsWithStep)
                .start(readWordsFromFileAndChangeCaseStep)
                .next(readWordsBackInAndReverseStep)
                .next(endsWithStep)
                .build();
    }

    private JobExecutionListener listener() {
        return new WordListener();
    }

    //Step Definition
    @Bean
    public Step readWordsFromFileAndChangeCaseStep(){
        return stepBuilderFactory.get("readWordsFromFileStep")
                .<Word,Word> chunk(100)
                .reader(readInTheWords(null))
                .processor(getWordUppercaseProcessor())
                .writer(wordWriter())
                .build();
    }

    @Bean
    public Step readWordsBackInAndReverseStep(){
        return stepBuilderFactory.get("readWordsBackInAndReverseStep")
                .<Word,Word>chunk(100)
                .reader(retrieveWordsFromDB())
                .processor(getWordFlipitProcessor())
                .writer(wordWriter())
                .build();
    }

    public RepositoryItemWriter<Word> wordWriter(){
        RepositoryItemWriter<Word> writer = new RepositoryItemWriter<>();
        writer.setMethodName("save");
        writer.setRepository(wordRepository);
        return writer;
    }

    WordReader getWordReader(){
        return new WordReader();
    }

    WordUpcaseProcessor getWordUppercaseProcessor(){
        return new WordUpcaseProcessor();
    }

    WordFlipitProcessor getWordFlipitProcessor(){
        return new WordFlipitProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemReader readInTheWords(@Value("#{jobParameters['fullPathFileName']}")String fullPathFileName){
        FlatFileItemReader ffr = new FlatFileItemReaderBuilder().name("read_in_words_from_file")
                .resource(new FileSystemResource("src/main/resources/"+fullPathFileName))
                .delimited()
                .names("word")
                .fieldSetMapper( new BeanWrapperFieldSetMapper(){
                    { setTargetType(Word.class);}
                })
                .build();
        return ffr;
    }

    @Bean
    public RepositoryItemReader<Word> retrieveWordsFromDB(){

        Map<String, Sort.Direction> sortMap = new HashMap<>();
        sortMap.put("id", Sort.Direction.ASC);

        RepositoryItemReader<Word> dbReader = new RepositoryItemReader<>();
        dbReader.setName("retrieveWordRecords");
        dbReader.setMethodName("findAll");
        dbReader.setRepository(wordRepository);
        dbReader.setSort(sortMap);

        return dbReader;

    }


   @Bean
   @StepScope
   public RepositoryItemReader<Word> dbReader(@Value("#{jobParameters['endsWith']}") String endsWith) {
        String ltr = "ow";
        log.info(endsWith);
        RepositoryItemReader<Word> wordRepositoryItemReader = new RepositoryItemReader<>();
        wordRepositoryItemReader.setRepository(wordRepository);
        wordRepositoryItemReader.setMethodName("findAllByFlippedEndingWith");
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("flipped", Sort.Direction.ASC);
        wordRepositoryItemReader.setSort(sorts);

        List<Object> queryMethodArguments = new ArrayList<>();
        queryMethodArguments.add(endsWith);
        wordRepositoryItemReader.setArguments(queryMethodArguments);



        return wordRepositoryItemReader;

    }
    @Bean
    public Step endsWithStep() {
        return stepBuilderFactory.get("endswith-step").<Word, Word>chunk(10)
                .reader(dbReader(null))
                .processor(endsWithProcessor())
                .writer(wordRepositoryItemWriter())
                .build();

    }


    private RepositoryItemWriter<Word> wordRepositoryItemWriter() {

        RepositoryItemWriter<Word> writer = new RepositoryItemWriter<>();
        writer.setRepository(wordRepository);
        writer.setMethodName("save");

        return writer;
    }

    private FlippedEndswithProcessor endsWithProcessor() {
        return new FlippedEndswithProcessor();
    }


}
