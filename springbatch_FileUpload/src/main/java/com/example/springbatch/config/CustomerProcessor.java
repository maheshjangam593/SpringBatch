package com.example.springbatch.config;

import com.example.springbatch.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer,Customer> {
    @Override
    public Customer process(Customer customer)  {
        int age=Integer.parseInt(customer.getAge());
        if(age>0)
        {
            return  customer;
        }
        return null;

    }
}
