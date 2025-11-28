package com.ingesoft.redsocial.excepciones;

public class ComentarioNotFoundException extends Exception {

    public ComentarioNotFoundException() {
        super();
    }

    public ComentarioNotFoundException(String message) {
        super(message);
    }

    public ComentarioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComentarioNotFoundException(Throwable cause) {
        super(cause);
    }
}
