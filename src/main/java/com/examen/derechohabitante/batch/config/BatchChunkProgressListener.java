package com.examen.derechohabitante.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchChunkProgressListener implements ChunkListener, StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BatchChunkProgressListener.class);

    private final int configuredChunkSize;
    private long writeCountAfterPreviousChunk;

    public BatchChunkProgressListener(@Value("${app.batch.chunk-size:5}") int configuredChunkSize) {
        this.configuredChunkSize = configuredChunkSize;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        writeCountAfterPreviousChunk = 0;
        log.info(
                "STEP '{}' — tamaño de chunk (commit cada): {} filas OK como máximo.",
                stepExecution.getStepName(),
                configuredChunkSize);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public void beforeChunk(ChunkContext context) {
        StepExecution step = context.getStepContext().getStepExecution();
        log.info(
                ">>> CHUNK abierto | leídas acumuladas={} | escritas acumuladas={}",
                step.getReadCount(),
                step.getWriteCount());
    }

    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution step = context.getStepContext().getStepExecution();
        long totalWrites = step.getWriteCount();
        long writtenInThisChunk = totalWrites - writeCountAfterPreviousChunk;
        writeCountAfterPreviousChunk = totalWrites;
        log.info(
                "<<< CHUNK cerrado (COMMIT) | filas persistidas en ESTE bloque={} | total OK={} | skips acum={}",
                writtenInThisChunk,
                totalWrites,
                step.getReadSkipCount() + step.getProcessSkipCount() + step.getWriteSkipCount());
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("<<< CHUNK con error — ROLLBACK del bloque actual (ninguna fila de este bloque se confirma).");
    }
}
