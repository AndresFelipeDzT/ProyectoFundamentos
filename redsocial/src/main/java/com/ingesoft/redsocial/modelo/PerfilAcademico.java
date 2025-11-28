package com.ingesoft.redsocial.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PerfilAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carrera;
    private String semestre;
    private String habilidades;

    @OneToOne
    @JoinColumn(name = "usuario_login")   // RELACIÓN CORRECTA
    @JsonIgnore   // evita bucle y problemas con JSON
    private Usuario usuario;
}
