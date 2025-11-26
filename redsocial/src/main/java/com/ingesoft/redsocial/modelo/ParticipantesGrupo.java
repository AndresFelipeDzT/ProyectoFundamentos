package com.ingesoft.redsocial.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ParticipantesGrupo {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Usuario usuario;

    @ManyToOne
    Grupo grupo;
}

