package com.examen.productos.dtos;

public record AuthResponse(
        String token,
        String type,
        String username
) {}
