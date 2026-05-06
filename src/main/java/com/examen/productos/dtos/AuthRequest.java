package com.examen.productos.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "El username es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
