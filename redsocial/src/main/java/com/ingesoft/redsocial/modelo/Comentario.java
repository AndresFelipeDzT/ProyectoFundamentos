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

    @OneToMany(mappedBy = "comentarioPadre", fetch = FetchType.LAZY)
    private List<Comentario> respuestas;

    @OneToMany(mappedBy = "comentario", fetch = FetchType.LAZY)
    private List<Reaccion> reacciones;
}
