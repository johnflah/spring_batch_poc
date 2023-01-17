package com.jof.batch.config.word;

import com.jof.batch.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {

    Page<Word> findAllByFlippedEndingWith(String ltr, Pageable pageable);
}
