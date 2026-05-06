package com.examen.productos.dtos;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        String descripcion
) {}
