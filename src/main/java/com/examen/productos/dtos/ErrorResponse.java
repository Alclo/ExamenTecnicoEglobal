package com.examen.productos.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String mensaje,
        LocalDateTime timestamp
) {}
