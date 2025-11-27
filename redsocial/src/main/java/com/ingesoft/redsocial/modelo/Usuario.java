package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Usuario {

    private String login;
    private String nombre;
    private String password;

    // Relaciones
    private List<SolicitudAmistad> solicitudesEnviadas = new ArrayList<>();
    private List<SolicitudAmistad> solicitudesRecibidas = new ArrayList<>();
    private List<Grupo> gruposCreados = new ArrayList<>();

    @JsonIgnore
    private PerfilAcademico perfil;

    private List<Publicacion> publicaciones = new ArrayList<>();
    private List<Comentario> comentarios = new ArrayList<>();

    public Usuario() {}

    public Usuario(String login, String nombre, String password) {
        this.login = login;
        this.nombre = nombre;
        this.password = password;
    }
}
