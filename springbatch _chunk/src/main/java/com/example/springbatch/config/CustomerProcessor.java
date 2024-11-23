package com.example.springbatch.config;

import com.example.springbatch.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer,Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        int age=Integer.parseInt(String.valueOf(customer.getAge()));
        if(customer.getAge()>0)
        {
            return  customer;
        }
        return null;

    }
}
