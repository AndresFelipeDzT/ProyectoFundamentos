package com.ingesoft.redsocial.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Reaccion;

@Repository
public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {
    boolean existsByComentarioIdAndUsuarioLogin(Long comentarioId, String login);
}
