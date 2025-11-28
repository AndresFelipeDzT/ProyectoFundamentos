package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Publicacion {

    @Id
    @GeneratedValue
    private Long id;

    private String contenido;

    private LocalDateTime fechaCreacion;

    private String rutaArchivo; // NUEVO: ruta de archivo adjunto (imagen, PDF, etc.)

    @ManyToOne
    private Usuario autor;

    @OneToMany(mappedBy = "publicacion")
    private List<Comentario> comentarios = new ArrayList<>();
}
