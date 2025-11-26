package com.ingesoft.redsocial.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByNombreGrupoContainingIgnoreCase(String nombre);

}
