package com.examen.derechohabitante.exception;

public class BatchIntegrityException extends RuntimeException {
    public BatchIntegrityException(String message) {
        super(message);
    }
}