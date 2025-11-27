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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_login")
    private Usuario creador;

    // Esta relación NO se usa directamente en Vaadin
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipantesGrupo> participantes = new ArrayList<>();

    public Grupo() {}

    public Long getId() { return id; }

    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }

    // ❗ Vaadin solo verá este número, no la lista
    public int getCantidadParticipantes() {
        return participantes != null ? participantes.size() : 0;
    }

    // ❗ Este getter NO se usa fuera de servicio
    public List<ParticipantesGrupo> getParticipantes() { 
        return participantes; 
    }
}
