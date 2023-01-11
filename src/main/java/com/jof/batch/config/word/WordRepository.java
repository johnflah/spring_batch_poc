package com.jof.batch.config.word;

import com.jof.batch.entity.MerchantEnablementStatus;
import com.jof.batch.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
}
