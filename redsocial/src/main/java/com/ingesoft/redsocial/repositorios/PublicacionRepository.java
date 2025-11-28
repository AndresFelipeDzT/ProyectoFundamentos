package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ingesoft.redsocial.modelo.Publicacion;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    // Carga todas las publicaciones con comentarios, autores y reacciones
    @Query("SELECT DISTINCT p FROM Publicacion p " +
           "LEFT JOIN FETCH p.comentarios c " +
           "LEFT JOIN FETCH p.autor a " +
           "LEFT JOIN FETCH c.autor " +
           "LEFT JOIN FETCH c.reacciones")
    List<Publicacion> findAllWithComentariosYAutor();

    // Carga una publicación específica con todos los comentarios y reacciones
    @Query("SELECT p FROM Publicacion p " +
           "LEFT JOIN FETCH p.comentarios c " +
           "LEFT JOIN FETCH p.autor a " +
           "LEFT JOIN FETCH c.autor " +
           "LEFT JOIN FETCH c.reacciones " +
           "WHERE p.id = :id")
    Optional<Publicacion> findByIdWithComentarios(Long id);

    Optional<Publicacion> findById(Long id);

     @Query("SELECT DISTINCT p FROM Publicacion p " +
           "LEFT JOIN FETCH p.comentarios c " +
           "LEFT JOIN FETCH c.reacciones " +
           "LEFT JOIN FETCH c.respuestas " +
           "LEFT JOIN FETCH p.autor " +
           "ORDER BY p.fechaCreacion DESC")
    List<Publicacion> findAllWithComentariosYReacciones();
}
