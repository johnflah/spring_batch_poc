package com.jof.batch.processor;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jof.batch.entity.Customer;
import com.jof.batch.entity.DummyCustomer;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;

@Component
@Slf4j
public class DummyCustomerChangeProcessor implements ItemProcessor<Customer, DummyCustomer> {

    @Override
    public DummyCustomer process(Customer customer) throws Exception {

        DummyCustomer dc = new DummyCustomer();
        dc.setName(customer.getFirstName());
        log.info("cus", dc);
        return dc;
    }

}