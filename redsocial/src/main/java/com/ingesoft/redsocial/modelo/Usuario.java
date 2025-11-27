package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Usuario {

    @Id
    private String login;
    private String nombre;
    private String password;

    @OneToMany(mappedBy = "remitente")
    private List<SolicitudAmistad> solicitudesEnviadas = new ArrayList<>();

    @OneToMany(mappedBy = "destinatario")
    private List<SolicitudAmistad> solicitudesRecibidas = new ArrayList<>();

    @OneToMany(mappedBy = "creador")
    private List<Grupo> gruposCreados = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private PerfilAcademico perfil;

    @OneToMany(mappedBy = "autor")
    private List<Publicacion> publicaciones = new ArrayList<>();

    @OneToMany(mappedBy = "autor")
    private List<Comentario> comentarios = new ArrayList<>();

    public Usuario() {}

    public Usuario(String login, String nombre, String password) {
        this.login = login;
        this.nombre = nombre;
        this.password = password;
    }
}
