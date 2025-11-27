package com.ingesoft.redsocial.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ParticipantesGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "usuario_login")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    public ParticipantesGrupo() {}

    public ParticipantesGrupo(Usuario usuario, Grupo grupo) {
        this.usuario = usuario;
        this.grupo = grupo;
    }

    public Long getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public Grupo getGrupo() { return grupo; }
}
