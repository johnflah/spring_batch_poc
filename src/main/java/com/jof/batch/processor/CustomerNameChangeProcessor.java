package com.jof.batch.processor;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jof.batch.entity.Customer;

import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;

@Component
public class CustomerNameChangeProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

        customer.setFirstName(customer.getFirstName()+"_" + LocalDateTime.now().toString());
        return customer;
    }

}