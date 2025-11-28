package com.ingesoft.redsocial.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Publicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByAutorLogin(String login);

    List<Publicacion> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT p FROM Publicacion p " +
        "LEFT JOIN FETCH p.comentarios c " +
        "LEFT JOIN FETCH c.reacciones " +
        "LEFT JOIN FETCH c.respuestas " +
        "WHERE p.id = :id")
    Publicacion findByIdConComentariosYReacciones(@Param("id") Long id);

}

