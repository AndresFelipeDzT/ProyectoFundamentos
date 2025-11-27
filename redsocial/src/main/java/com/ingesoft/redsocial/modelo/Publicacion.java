package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Publicacion {

    private Long id;
    private String contenido;
    private LocalDateTime fechaCreacion;

    private Usuario autor;
    private List<Comentario> comentarios = new ArrayList<>();
}
