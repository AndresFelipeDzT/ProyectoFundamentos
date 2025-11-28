package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Publicacion {

    @Id
    @GeneratedValue
    private Long id;

    private String contenido;
    private LocalDateTime fechaCreacion;

    private String rutaArchivo; // NUEVO: ruta de archivo adjunto

    @ManyToOne
    private Usuario autor;

    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();
}
