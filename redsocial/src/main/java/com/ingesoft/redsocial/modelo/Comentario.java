package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Comentario {

    private Long id;
    private String texto;
    private LocalDateTime fecha;

    private Usuario autor;
    private Publicacion publicacion;
}
