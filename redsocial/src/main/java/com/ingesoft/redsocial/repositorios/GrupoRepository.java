package com.ingesoft.redsocial.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    Optional<Grupo> findByNombreGrupo(String nombreGrupo);
    Optional<Grupo> findByNombreGrupoIgnoreCase(String nombreGrupo);
    List<Grupo> findByNombreGrupoContainingIgnoreCase(String nombre);

}
