package com.example.springbatch.config.service;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<Integer,Long> {
    @Override
    public Long process(Integer item) throws Exception {
        System.out.println("In firstItem processor");
        return Long.valueOf(item+20);
    }
}
