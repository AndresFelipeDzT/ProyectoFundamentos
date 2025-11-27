package com.ingesoft.redsocial.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombreGrupo;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_login")
    @JsonIgnore
    private Usuario creador;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
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

    @JsonIgnore
    public List<ParticipantesGrupo> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipantesGrupo> participantes) {
        this.participantes = participantes;
    }

    // Método seguro que NO expone entidades
    public List<String> getLoginsParticipantes() {
        List<String> lista = new ArrayList<>();
        for (ParticipantesGrupo p : participantes) {
            lista.add(p.getUsuario().getLogin());
        }
        return lista;
    }
}
