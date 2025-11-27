package com.ingesoft.redsocial.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PerfilAcademico {

    private Long id;
    private String carrera;
    private String semestre;
    private String habilidades;

    @JsonIgnore
    private Usuario usuario;
}
