package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "publicacion", fetch = FetchType.LAZY)
    private Set<Comentario> comentarios = new HashSet<>();
    
}
