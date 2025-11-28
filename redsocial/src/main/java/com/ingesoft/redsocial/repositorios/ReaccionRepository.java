package com.ingesoft.redsocial.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ingesoft.redsocial.modelo.Reaccion;
import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Usuario;

public interface ReaccionRepository extends JpaRepository<Reaccion, Long> {

    Optional<Reaccion> findByComentarioAndAutor(Comentario comentario, Usuario autor);
}
