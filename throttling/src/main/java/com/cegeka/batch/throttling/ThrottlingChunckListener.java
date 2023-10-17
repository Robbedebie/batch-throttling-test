package com.cegeka.batch.throttling;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import static java.lang.Thread.sleep;

public class ThrottlingChunckListener implements ChunkListener {

    private long delay = 1000;

    @Override
    public void afterChunk(ChunkContext context) {
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
