package com.jof.batch.config.word;

import com.jof.batch.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WordBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final WordRepository wordRepository;



    @Bean("importwordsjob")
    public Job importWords(Step readWordsFromFileAndChangeCaseStep, Step readWordsBackInAndReverseStep ){
        return jobBuilderFactory.get("importWords")
                .incrementer(new RunIdIncrementer())
                .start(readWordsFromFileAndChangeCaseStep)
                .next(readWordsBackInAndReverseStep)
                .build();
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
}
