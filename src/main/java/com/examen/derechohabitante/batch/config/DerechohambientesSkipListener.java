package com.examen.derechohabitante.batch.config;

import com.examen.derechohabitante.dtos.DerechohambientesRequestDTO;
import com.examen.derechohabitante.models.Derechohambientes;
import com.examen.derechohabitante.service.BatchErrorLogService;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DerechohambientesSkipListener implements SkipListener<DerechohambientesRequestDTO, Derechohambientes> {

    private final BatchErrorLogService errorLogService;
    private final String inputFileReportName;

    public DerechohambientesSkipListener(
            BatchErrorLogService errorLogService,
            @Value("${app.batch.input-file}") String inputPath) {
        this.errorLogService = errorLogService;
        this.inputFileReportName = shortenPathForDb(inputPath != null ? inputPath : "");
    }

    @Override
    public void onSkipInProcess(DerechohambientesRequestDTO item, Throwable t) {
        errorLogService.save(
                "DerechohambientesJob_PROCESS_ERROR",
                inputFileReportName,
                item != null ? item.toString() : "N/A",
                mapProcessMessage(t));
    }

    @Override
    public void onSkipInRead(Throwable t) {
        String rawData = "N/A";
        if (t instanceof FlatFileParseException e) {
            rawData = e.getInput();
        }
        errorLogService.save(
                "DerechohambientesJob_READ_ERROR",
                inputFileReportName,
                rawData,
                mapReadMessage(t));
    }

    @Override
    public void onSkipInWrite(Derechohambientes item, Throwable t) {
        errorLogService.save(
                "DerechohambientesJob_WRITE_ERROR",
                inputFileReportName,
                item != null ? item.toString() : "N/A",
                mapProcessMessage(t));
    }

    private static String shortenPathForDb(String path) {
        if (path.isEmpty()) {
            return "";
        }
        if (path.startsWith("classpath:")) {
            return path.substring("classpath:".length());
        }
        int slash = Math.max(path.lastIndexOf('\\'), path.lastIndexOf('/'));
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static String mapReadMessage(Throwable t) {
        if (t instanceof IncorrectTokenCountException ix) {
            return "FORMATO FILA: se esperaban exactamente "
                    + ix.getExpectedCount()
                    + " columnas (;), se leyeron "
                    + ix.getActualCount()
                    + ". "
                    + ix.getMessage();
        }
        if (t instanceof FlatFileParseException e) {
            String causeDetail = "";
            if (e.getCause() instanceof IncorrectTokenCountException ix) {
                causeDetail =
                        " Causa: esperadas " + ix.getExpectedCount() + " columnas, leídas " + ix.getActualCount() + ".";
            }
            return "INTEGRIDAD LECTURA línea "
                    + e.getLineNumber()
                    + ": se esperaban 5 campos separados por punto y coma (;) por línea."
                    + causeDetail;
        }
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }

    private static String mapProcessMessage(Throwable t) {
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }
}
