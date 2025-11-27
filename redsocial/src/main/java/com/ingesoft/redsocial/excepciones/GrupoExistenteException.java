package com.ingesoft.redsocial.excepciones;

public class GrupoExistenteException extends RuntimeException {
    public GrupoExistenteException(String nombreGrupo) {
        super("Ya existe un grupo con el nombre: " + nombreGrupo);
    }
}
