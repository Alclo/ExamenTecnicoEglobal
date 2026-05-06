package com.examen.derechohabitante.dtos;

import java.math.BigDecimal;

public record DerechohambientesResponseDTO(
        Integer id,
        String nombre,
        String ciudad,
        BigDecimal importe,
        String cuenta) {}
