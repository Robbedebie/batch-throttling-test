package com.cegeka.batch.throttling;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThrottlingBatchItemReader implements ItemReader<DummyPerson> {

    private Iterator<DummyPerson> iterator;

    public ThrottlingBatchItemReader() {
        List<DummyPerson> persons = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            persons.add(new DummyPerson());
        }
        this.iterator = persons.iterator();
    }

    @Override
    public DummyPerson read(){
        if (iterator.hasNext()) {
            DummyPerson nextPerson = iterator.next();
            System.out.println(nextPerson);
            return nextPerson;
        }
        return null;
    }
}
