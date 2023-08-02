package com.jof.batch.processor;

import org.springframework.stereotype.Component;

import com.jof.batch.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

@Component
public class CustomerCapitalCaseProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

        customer.setFirstName(customer.getFirstName().toUpperCase());
        customer.setLastName(customer.getLastName().toUpperCase());

        return customer;
    }

}