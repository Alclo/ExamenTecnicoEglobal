package com.examen.derechohabitante.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchErrorLogService {

    private static final Logger log = LoggerFactory.getLogger(BatchErrorLogService.class);

    private final JdbcTemplate jdbcTemplate;

    public BatchErrorLogService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(String jobPrefix, String fileName, String rawData, String errorMessage) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO batch_error_logs (job_name, file_name, raw_data, error_message, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)",
                    jobPrefix,
                    fileName,
                    rawData,
                    errorMessage
            );
        } catch (IllegalArgumentException | org.springframework.jdbc.CannotGetJdbcConnectionException e) {
            log.warn("No se pudo persistir batch_error_logs: {}", e.getMessage());
        }
    }
}
