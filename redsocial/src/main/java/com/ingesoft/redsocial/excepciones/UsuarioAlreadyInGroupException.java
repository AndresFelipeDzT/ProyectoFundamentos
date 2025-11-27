package com.ingesoft.redsocial.excepciones;

public class UsuarioAlreadyInGroupException extends RuntimeException {
    public UsuarioAlreadyInGroupException(String message) {
        super(message);
    }
}
