package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Publicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    // Carga todas las publicaciones con comentarios, autores y respuestas de los comentarios
    @EntityGraph(attributePaths = {
        "autor",
        "comentarios",
        "comentarios.autor",
        "comentarios.respuestas",
        "comentarios.respuestas.autor"
    })
    List<Publicacion> findAllByOrderByFechaCreacionDesc();

    // Carga una publicación específica con comentarios, autores y respuestas
    @EntityGraph(attributePaths = {
        "autor",
        "comentarios",
        "comentarios.autor",
        "comentarios.respuestas",
        "comentarios.respuestas.autor"
    })
    Optional<Publicacion> findById(Long id);

    // Trae todos los comentarios de una publicación específica
    @EntityGraph(attributePaths = {
        "comentarios",
        "comentarios.autor",
        "comentarios.respuestas",
        "comentarios.respuestas.autor"
    })
    List<Publicacion> findByIdOrderByFechaCreacionDesc(Long id);

    @Query("SELECT DISTINCT p FROM Publicacion p " +
       "LEFT JOIN FETCH p.comentarios c " +
       "LEFT JOIN FETCH c.autor " +
       "WHERE p.id = :id")
Optional<Publicacion> findByIdWithComentarios(Long id);

}
