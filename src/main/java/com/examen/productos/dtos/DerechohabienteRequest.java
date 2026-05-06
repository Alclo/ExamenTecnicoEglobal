package com.examen.productos.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DerechohabienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El importe debe ser mayor a 0")
        BigDecimal importe,

        @NotNull(message = "La cuenta es obligatoria")
        Long cuenta
) {}
