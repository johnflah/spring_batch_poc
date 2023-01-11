package com.jof.batch.config.word;

import com.jof.batch.entity.Word;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class WordUpcaseProcessor implements ItemProcessor<Word,Word> {
    @Override
    public Word process(Word word) throws Exception {
      log.info("word is {}",word);
      word.setFlipped(word.getWord().toUpperCase());
        log.info("word is now {}",word);
        return word;
    }
}
