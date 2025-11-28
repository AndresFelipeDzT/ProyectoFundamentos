package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comentario {

    @Id
    @GeneratedValue
    private Long id;

    private String texto;

    private LocalDateTime fecha;

    @ManyToOne
    private Usuario autor;

    @ManyToOne
    private Publicacion publicacion;

    @ManyToOne
    private Comentario comentarioPadre; // Para respuestas

    @OneToMany(mappedBy = "comentario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaccion> reacciones = new ArrayList<>();

    @OneToMany(mappedBy = "comentarioPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> respuestas = new ArrayList<>();
}
