package com.ingesoft.redsocial.excepciones;

public class DuplicateUsuarioException extends Exception {
    public DuplicateUsuarioException(String message) {
        super(message);
    }
}
