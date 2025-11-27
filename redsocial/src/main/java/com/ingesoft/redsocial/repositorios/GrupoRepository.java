package com.ingesoft.redsocial.repositorios;

import com.ingesoft.redsocial.modelo.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    boolean existsByNombreGrupoIgnoreCase(String nombreGrupo);

    // Opcionales si los quieres usar en búsquedas
    Grupo findByNombreGrupo(String nombreGrupo);

    Grupo findByNombreGrupoIgnoreCase(String nombreGrupo);
}
