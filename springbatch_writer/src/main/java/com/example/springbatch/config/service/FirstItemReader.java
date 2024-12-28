package com.example.springbatch.config.service;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FirstItemReader implements ItemReader<Integer> {
    List<Integer> list=new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
    int i=0;
    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("In FirstItem Reader");
        if(i<list.size())
        {
            int item=list.get(i);
            i++;
            return item;
        }
        i=0;
        return null;
    }
}
