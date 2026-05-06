package com.examen.derechohabitante.batch.config;

import com.examen.derechohabitante.dtos.DerechohambientesRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

@Component
public class BatchIntegrityListener implements ItemProcessListener<DerechohambientesRequestDTO, Object> {
    private static final Logger log = LoggerFactory.getLogger(BatchIntegrityListener.class);

    @Override
    public void onProcessError(DerechohambientesRequestDTO item, Exception e) {
        Integer id = item != null ? item.id() : null;
        log.error("INTEGRIDAD GLOBAL en registro id={}: {}", id, e.getMessage());
    }
}
