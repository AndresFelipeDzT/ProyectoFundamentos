package com.ingesoft.redsocial.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPublicacionIdAndComentarioPadreIsNullOrderByFechaAsc(Long publicacionId);
}
