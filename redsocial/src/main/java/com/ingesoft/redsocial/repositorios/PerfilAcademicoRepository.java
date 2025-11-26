package com.ingesoft.redsocial.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.PerfilAcademico;

@Repository
public interface PerfilAcademicoRepository extends JpaRepository<PerfilAcademico, Long> {

    PerfilAcademico findByUsuarioLogin(String login);

}

