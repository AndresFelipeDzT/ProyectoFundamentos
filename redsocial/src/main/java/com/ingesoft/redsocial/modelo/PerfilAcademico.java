package com.ingesoft.redsocial.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class PerfilAcademico {

    @Id
    @GeneratedValue
    Long id;

    String carrera;
    String semestre;
    String habilidades;

    @OneToOne
    Usuario usuario;

}
