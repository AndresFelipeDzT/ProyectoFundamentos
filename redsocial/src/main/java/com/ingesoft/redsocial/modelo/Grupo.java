package com.ingesoft.redsocial.modelo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Grupo {

    @Id
    @GeneratedValue
    private Long id;
    
    private String nombreGrupo;
    private String descripcion;

    @ManyToOne
    @JsonIgnore
    private Usuario creador;

    @OneToMany(mappedBy = "grupo",fetch = FetchType.LAZY)
    private List<ParticipantesGrupo> participantes = new ArrayList<>();
}

