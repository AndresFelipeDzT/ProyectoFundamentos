package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Comentario {

    @Id
    @GeneratedValue
    Long id;

    String texto;

    LocalDateTime fecha;

    @ManyToOne
    Usuario autor;

    @ManyToOne
    Publicacion publicacion;
}
