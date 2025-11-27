package com.ingesoft.redsocial.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

@Data
public class ParticipantesGrupo {

    private Long id; // Puedes seguir usando System.currentTimeMillis() o un contador para IDs

    private Usuario usuario;

    @JsonBackReference // Evita loop al serializar Grupo
    private Grupo grupo;
}
