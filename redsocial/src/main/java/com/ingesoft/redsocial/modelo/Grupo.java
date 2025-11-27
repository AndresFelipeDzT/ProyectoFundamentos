package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreGrupo;
    private String descripcion;

    @ManyToOne
    private Usuario creador;

    @ManyToMany
    @JoinTable(
        name = "grupo_participantes",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_login")
    )
    private List<Usuario> participantes = new ArrayList<>();

    public Grupo() {}

    public Grupo(String nombreGrupo, String descripcion, Usuario creador) {
        this.nombreGrupo = nombreGrupo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.participantes.add(creador); // El creador se agrega automáticamente
    }
}
