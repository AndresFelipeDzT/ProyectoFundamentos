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
    private Comentario comentarioPadre;

    @OneToMany(mappedBy = "comentarioPadre", fetch = FetchType.LAZY)
    private Set<Comentario> respuestas = new HashSet<>();

    @OneToMany(mappedBy = "comentario", fetch = FetchType.LAZY)
    private Set<Reaccion> reacciones = new HashSet<>();
}