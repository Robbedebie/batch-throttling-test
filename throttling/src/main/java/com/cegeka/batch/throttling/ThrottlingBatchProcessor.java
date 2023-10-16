package com.cegeka.batch.throttling;

import org.springframework.batch.item.ItemProcessor;

public class ThrottlingBatchProcessor implements ItemProcessor<DummyPerson, DummyPerson> {

    @Override
    public DummyPerson process(DummyPerson item) throws Exception {
        System.out.println("ThrottlingBatchProcessor: " + item.toString());
        return item;
    }
}
