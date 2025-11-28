package com.ingesoft.redsocial.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reaccion {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Comentario comentario;

    @Enumerated(EnumType.STRING)
    private TipoReaccion tipo;

    public enum TipoReaccion {
        LIKE, DISLIKE
    }
}
