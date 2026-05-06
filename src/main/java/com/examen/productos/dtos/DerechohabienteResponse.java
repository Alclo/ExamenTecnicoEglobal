package com.examen.productos.dtos;

import java.math.BigDecimal;

public record DerechohabienteResponse(
        Long id,
        String nombre,
        BigDecimal importe,
        Long cuenta
) {}
