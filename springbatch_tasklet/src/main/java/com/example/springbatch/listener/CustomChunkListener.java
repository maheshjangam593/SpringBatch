package com.example.springbatch.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CustomChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext chunkContext)
    {
        System.out.println("about to start chunk "+chunkContext.getStepContext());
    }

    @Override
    public void afterChunk(ChunkContext context) {
        long writeCount = context.getStepContext().getStepExecution().getWriteCount();
        System.out.println("chunk write count "+ writeCount);
    }
}
