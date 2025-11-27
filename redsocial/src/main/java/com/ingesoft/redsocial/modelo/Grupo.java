package com.ingesoft.redsocial.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreGrupo;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "creador_login")
    private Usuario creador;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantesGrupo> participantes = new ArrayList<>();

    public Grupo() {}

    public Grupo(String nombreGrupo, String descripcion, Usuario creador) {
        this.nombreGrupo = nombreGrupo;
        this.descripcion = descripcion;
        this.creador = creador;
    }

    public Long getId() {
        return id;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public List<ParticipantesGrupo> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipantesGrupo> participantes) {
        this.participantes = participantes;
    }

    // ✅ Variable básica: número de participantes
    public int getCantidadParticipantes() {
        return participantes != null ? participantes.size() : 0;
    }
}
