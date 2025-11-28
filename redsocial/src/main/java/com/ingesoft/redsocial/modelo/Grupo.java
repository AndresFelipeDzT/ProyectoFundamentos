package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreGrupo;
    private String descripcion;

    // 👇 RELACIÓN QUE FALTABA (coherente con Usuario.gruposCreados)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_login")
    private Usuario creador;

    @JsonIgnore
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantesGrupo> participantes = new ArrayList<>();

    public Grupo() {}

    // Getters y setters necesarios
    public Long getId() { return id; }

    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }

    public List<ParticipantesGrupo> getParticipantes() { return participantes; }
}
