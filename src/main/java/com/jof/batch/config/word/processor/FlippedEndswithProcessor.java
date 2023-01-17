package com.jof.batch.config.word.processor;

import com.jof.batch.entity.Word;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class FlippedEndswithProcessor implements ItemProcessor<Word,Word> {

    @Override
    public Word process(Word word) throws Exception {
        log.info(word.getFlipped());
        return word;
    }
}
