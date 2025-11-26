package com.ingesoft.redsocial.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ingesoft.redsocial.modelo.ParticipantesGrupo;

@Repository
public interface ParticipantesGrupoRepository extends JpaRepository<ParticipantesGrupo, Long> {

    List<ParticipantesGrupo> findByGrupoId(Long id);

    boolean existsByGrupoIdAndUsuarioLogin(Long grupoId, String login);
}
