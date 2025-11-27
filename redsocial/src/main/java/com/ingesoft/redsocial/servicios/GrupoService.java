package com.ingesoft.redsocial.servicios;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.GrupoRepository;
import com.ingesoft.redsocial.repositorios.ParticipantesGrupoRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipantesGrupoRepository participantesRepo;

    // Crear grupo -----------------------------------------
    @Transactional
    public Grupo crearGrupo(String nombre, String descripcion, String creadorLogin) {

        if (grupoRepository.existsByNombreGrupoIgnoreCase(nombre)) {
            throw new RuntimeException("Ya existe un grupo con ese nombre");
        }

        Usuario creador = usuarioRepository.findById(creadorLogin)
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        Grupo grupo = new Grupo(nombre, descripcion, creador);
        grupoRepository.save(grupo);

        // El creador entra automáticamente
        participantesRepo.save(new ParticipantesGrupo(creador, grupo));

        return grupo;
    }

    // Obtener todos ----------------------------------------
    public List<Grupo> obtenerTodos() {
        return grupoRepository.findAll();
    }

    // Cargar nombres realmente dentro de la transacción ----
    @Transactional(readOnly = true)
    public List<String> obtenerNombresParticipantes(Long grupoId) {
        return participantesRepo.findByGrupoId(grupoId)
                .stream()
                .map(pg -> pg.getUsuario().getLogin())
                .toList();
    }

    // Agregar un usuario al grupo --------------------------
    @Transactional
    public void agregarParticipante(Long grupoId, String loginUsuario) {

        boolean yaExiste = participantesRepo.existsByGrupoIdAndUsuarioLogin(grupoId, loginUsuario);
        if (yaExiste) {
            throw new RuntimeException("El usuario ya pertenece al grupo");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Usuario usuario = usuarioRepository.findById(loginUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ParticipantesGrupo participante = new ParticipantesGrupo(usuario, grupo);
        participantesRepo.save(participante);
    }
}
