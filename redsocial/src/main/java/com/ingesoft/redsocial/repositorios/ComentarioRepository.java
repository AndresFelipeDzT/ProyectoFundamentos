package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @EntityGraph(attributePaths = {"autor"})
    List<Comentario> findByPublicacionId(Long publicacionId);

    @EntityGraph(attributePaths = {"autor"})
    Optional<Comentario> findById(Long id);
}
