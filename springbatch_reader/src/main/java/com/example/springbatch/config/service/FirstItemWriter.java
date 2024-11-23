package com.example.springbatch.config.service;


import com.example.springbatch.entity.StudentCsv;
import com.example.springbatch.entity.StudentJdbc;
import com.example.springbatch.entity.StudentJson;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;


@Component
public class FirstItemWriter implements ItemWriter<StudentJdbc> {


    @Override
    public void write(Chunk<? extends StudentJdbc> item) throws Exception {
        System.out.println("In writer " + StudentJdbc.class.getName());
        System.out.println(item);
    }
}
