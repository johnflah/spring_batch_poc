package com.jof.batch.config;

import com.jof.batch.entity.MerchantEnablementStatus;
import org.springframework.batch.item.ItemProcessor;

public class MidProcessor implements ItemProcessor<MerchantEnablementStatus, MerchantEnablementStatus> {
    @Override
    public MerchantEnablementStatus process(MerchantEnablementStatus merchantEnablementStatus) throws Exception {
        merchantEnablementStatus.setCustomer(Long.parseLong(merchantEnablementStatus.getMid()) % 3 == 0 ? "TT":"FF");

        return merchantEnablementStatus;
    }
}
