package com.examen.derechohabitante.batch.config;

import com.examen.derechohabitante.dtos.DerechohambientesRequestDTO;
import com.examen.derechohabitante.exception.BatchIntegrityException;
import com.examen.derechohabitante.models.Derechohambientes;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    @Bean
    @StepScope
    public FlatFileItemReader<DerechohambientesRequestDTO> reader(
            @Value("${app.batch.input-file}") String inputLocation) {
        Resource resource = resolveBatchInput(inputLocation);
        log.info(
                "Job batch: archivo de entrada = {} | existe/disponible = {}",
                resource.getDescription(),
                resource.exists());

        FlatFileItemReader<DerechohambientesRequestDTO> reader = new FlatFileItemReader<>();
        reader.setName("derechohambientesReader");
        reader.setResource(resource);
        reader.setEncoding(StandardCharsets.UTF_8.name());
        reader.setLineMapper(new StrictFiveSemicolonFieldsLineMapper());
        return reader;
    }

    private static Resource resolveBatchInput(String location) {
        String loc =
                location == null || location.isBlank() ? "classpath:derechohambientes.txt" : location;
        if (loc.startsWith("classpath:")) {
            return new ClassPathResource(loc.substring("classpath:".length()));
        }
        return new FileSystemResource(loc);
    }

    @Bean
    public JpaItemWriter<Derechohambientes> writer(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<Derechohambientes>()
                .entityManagerFactory(emf)
                .build();
    }

    @Bean
    public Step step1(
            JobRepository jobRepository,
            PlatformTransactionManager tm,
            FlatFileItemReader<DerechohambientesRequestDTO> reader,
            JpaItemWriter<Derechohambientes> writer,
            DerechohambientesSkipListener skipListener,
            BatchIntegrityListener integrityListener,
            BatchChunkProgressListener chunkProgressListener,
            @Value("${app.batch.chunk-size:5}") int chunkSize) {

        return new StepBuilder("step1", jobRepository)
                .<DerechohambientesRequestDTO, Derechohambientes>chunk(chunkSize, tm)
                .reader(reader)
                .processor(item -> {
                    String cuentaLimpia = item.cuenta().trim();
                    if (cuentaLimpia.length() != 16 || !cuentaLimpia.chars().allMatch(Character::isDigit)) {
                        throw new BatchIntegrityException(
                                "INTEGRIDAD CUENTA: debe ser numérica de 16 dígitos. Valor: " + cuentaLimpia);
                    }

                    return Derechohambientes.builder()
                            .id(item.id())
                            .nombre(item.nombre().trim())
                            .ciudad(item.ciudad().trim())
                            .importe(item.importe())
                            .cuenta(cuentaLimpia)
                            .build();
                })
                .listener(integrityListener)
                .writer(writer)
                .faultTolerant()
                .skip(BatchIntegrityException.class)
                .skip(FlatFileParseException.class)
                .skip(IncorrectTokenCountException.class)
                .skipLimit(50)
                .listener(skipListener)
                .listener((ChunkListener) chunkProgressListener)
                .listener((StepExecutionListener) chunkProgressListener)
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobExecutionListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("================================================");
                log.info("   ESTADO FINAL DEL JOB: {}", jobExecution.getStatus());
                jobExecution.getStepExecutions().forEach(step -> {
                    long leidos = step.getReadCount();
                    long escritos = step.getWriteCount();
                    long omitidos =
                            step.getReadSkipCount() + step.getProcessSkipCount() + step.getWriteSkipCount();
                    log.info("   - Total líneas/leídas: {}", leidos);
                    log.info("   - Registros procesados OK (persistidos): {}", escritos);
                    log.info("   - Registros omitidos por error / integridad (fallidos en batch): {}", omitidos);
                });
                log.info("================================================");
            }
        };
    }

    static final class StrictFiveSemicolonFieldsLineMapper implements LineMapper<DerechohambientesRequestDTO> {

        static final int EXPECTED_FIELDS = 5;

        @Override
        public DerechohambientesRequestDTO mapLine(String line, int lineNumber) throws Exception {
            if (line == null) {
                throw new FlatFileParseException("Línea nula", "", lineNumber);
            }
            String raw = stripBom(line).strip();
            if (raw.isEmpty()) {
                throw new FlatFileParseException("Línea vacía", line, lineNumber);
            }
            String[] parts = raw.split(";", -1);
            if (parts.length != EXPECTED_FIELDS) {
                String msg =
                        String.format(
                                "Se esperaban %d campos separados por ';' (id;nombre;ciudad;importe;cuenta); "
                                        + "esta línea tiene %d campo(s).",
                                EXPECTED_FIELDS, parts.length);
                throw new FlatFileParseException(
                        msg,
                        new IncorrectTokenCountException(EXPECTED_FIELDS, parts.length),
                        raw,
                        lineNumber);
            }
            try {
                Integer id = Integer.valueOf(parts[0].trim());
                String nombre = parts[1].trim();
                String ciudad = parts[2].trim();
                BigDecimal importe = new BigDecimal(parts[3].trim().replace(',', '.'));
                String cuenta = parts[4].trim();
                return new DerechohambientesRequestDTO(id, nombre, ciudad, importe, cuenta);
            } catch (RuntimeException ex) {
                throw new FlatFileParseException(
                        "Error parseando tipos de la línea", ex, raw, lineNumber);
            }
        }

        private static String stripBom(String line) {
            if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                return line.substring(1);
            }
            return line;
        }
    }
}
