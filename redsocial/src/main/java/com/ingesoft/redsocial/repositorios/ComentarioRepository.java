package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ingesoft.redsocial.modelo.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    // Trae los comentarios de una publicación con autor y respuestas
    @EntityGraph(attributePaths = {"autor", "respuestas", "respuestas.autor"})
    List<Comentario> findByPublicacionId(Long publicacionId);

    // Trae un comentario específico con autor y respuestas
    @EntityGraph(attributePaths = {"autor", "respuestas", "respuestas.autor"})
    Optional<Comentario> findById(Long id);
}
