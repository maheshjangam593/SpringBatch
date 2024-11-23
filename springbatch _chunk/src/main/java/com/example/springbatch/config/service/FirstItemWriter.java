package com.example.springbatch.config.service;


import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;


@Component
public class FirstItemWriter implements ItemWriter<Long> {


    @Override
    public void write(Chunk<? extends Long> item) throws Exception {
        System.out.println("In writer ");
        System.out.println(item);
    }
}
