package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Publicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    @EntityGraph(attributePaths = {"autor", "comentarios", "comentarios.autor"})
    List<Publicacion> findAllByOrderByFechaCreacionDesc();

    @EntityGraph(attributePaths = {"autor", "comentarios", "comentarios.autor"})
    Optional<Publicacion> findById(Long id);
}
