package com.project.neoexplorer.service;

public class TranslationException extends RuntimeException {
    public TranslationException(String mensaje) {
        super(mensaje);
    }

    public TranslationException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}