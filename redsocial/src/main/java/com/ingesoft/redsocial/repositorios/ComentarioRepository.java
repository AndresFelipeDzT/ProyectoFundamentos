package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Publicacion;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    // Carga comentario con todas sus respuestas y reacciones
    @Query("SELECT c FROM Comentario c " +
           "LEFT JOIN FETCH c.reacciones " +
           "LEFT JOIN FETCH c.respuestas " +
           "LEFT JOIN FETCH c.autor " +
           "WHERE c.id = :id")
    Optional<Comentario> findByIdWithRespuestasYReacciones(Long id);

    List<Comentario> findByPublicacion(Publicacion publicacion);
}
