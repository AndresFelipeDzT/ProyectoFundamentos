package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AppData {
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();
    private List<ParticipantesGrupo> participantes = new ArrayList<>();
    private List<SolicitudAmistad> solicitudesAmistad = new ArrayList<>();
    // Puedes agregar publicaciones, comentarios, etc.
}
