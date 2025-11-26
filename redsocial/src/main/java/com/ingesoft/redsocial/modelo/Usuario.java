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
    String login;

    String nombre;

    String password;


    @OneToMany(mappedBy = "remitente")
    List<SolicitudAmistad> solicitudesEnviadas = new ArrayList<>();

    @OneToMany(mappedBy = "destinatario")
    List<SolicitudAmistad> solicitudesRecibidas = new ArrayList<>();

    @OneToMany(mappedBy = "creador")
    List<Grupo> gruposCreados = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    PerfilAcademico perfil;

    @OneToMany(mappedBy = "autor")
    List<Publicacion> publicaciones = new ArrayList<>();

    @OneToMany(mappedBy = "autor")
    List<Comentario> comentarios = new ArrayList<>();


    public Usuario() {

    }

    public Usuario(String login, String nombre, String password) {
        this.login = login;
        this.nombre = nombre;
        this.password = password;
    }

}
