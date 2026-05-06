package com.examen.productos.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DerechohabientePatchRequest(
        String nombre,

        @DecimalMin(value = "0.0", inclusive = false, message = "El importe debe ser mayor a 0")
        BigDecimal importe,

        @Positive(message = "La cuenta debe ser un número positivo")
        Long cuenta
) {}
