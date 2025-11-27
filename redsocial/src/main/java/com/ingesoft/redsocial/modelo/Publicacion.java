package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Publicacion {

    @Id
    @GeneratedValue
    Long id;

    String contenido;

    LocalDateTime fechaCreacion;

    @ManyToOne
    Usuario autor;

    @OneToMany(mappedBy = "publicacion")
    List<Comentario> comentarios = new ArrayList<>();

}

