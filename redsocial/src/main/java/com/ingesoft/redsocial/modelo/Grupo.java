package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
public class Grupo {

    private Long id; // Generado manualmente con System.currentTimeMillis() o Random

    private String nombreGrupo;
    private String descripcion;

    @JsonIgnore
    private Usuario creador;

    @JsonManagedReference
    private List<ParticipantesGrupo> participantes = new ArrayList<>();
}
