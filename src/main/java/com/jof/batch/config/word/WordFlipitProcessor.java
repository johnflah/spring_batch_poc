package com.jof.batch.config.word;

import com.jof.batch.entity.Word;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class WordFlipitProcessor implements ItemProcessor<Word,Word> {
    @Override
    public Word process(Word word) throws Exception {
//      log.info("word is {}",word);
      String rr = new StringBuilder(word.getWord()).reverse().toString();
      word.setFlipped(rr);
//        log.info("word is now {}",word);
        return word;
    }
}
