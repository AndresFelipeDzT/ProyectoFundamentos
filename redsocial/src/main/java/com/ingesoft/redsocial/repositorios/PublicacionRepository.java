package com.ingesoft.redsocial.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Publicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByAutorLogin(String login);

    List<Publicacion> findAllByOrderByFechaCreacionDesc();

}

