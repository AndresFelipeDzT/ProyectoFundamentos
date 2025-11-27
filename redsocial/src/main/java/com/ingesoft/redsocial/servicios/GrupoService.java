package com.ingesoft.redsocial.servicios;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.*;
import com.ingesoft.redsocial.modelo.*;
import com.ingesoft.redsocial.repositorios.*;

@Service
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepo;
    private final UsuarioRepository usuarioRepo;

    public GrupoService(GrupoRepository grupoRepo, UsuarioRepository usuarioRepo) {
        this.grupoRepo = grupoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion)
            throws UsuarioNotFoundException, GrupoExistenteException {

        if (grupoRepo.findByNombreGrupoIgnoreCase(nombre).isPresent()) {
            throw new GrupoExistenteException("Ya existe un grupo con ese nombre");
        }

        Usuario creador = usuarioRepo.findById(loginCreador)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        // Agregar creador como participante
        ParticipantesGrupo participanteCreador = new ParticipantesGrupo(creador, grupo);
        grupo.getParticipantes().add(participanteCreador);

        return grupoRepo.save(grupo);
    }

    public void unirseAGrupo(String loginUsuario, Long grupoId)
            throws UsuarioNotFoundException, GrupoNotFoundException, UsuarioAlreadyInGroupException {

        Grupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado"));

        Usuario usuario = usuarioRepo.findById(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        boolean yaParticipa = grupo.getParticipantes().stream()
                .anyMatch(p -> p.getUsuario().getLogin().equals(loginUsuario));

        if (yaParticipa) {
            throw new UsuarioAlreadyInGroupException("Ya eres miembro de este grupo");
        }

        ParticipantesGrupo nuevoParticipante = new ParticipantesGrupo(usuario, grupo);
        grupo.getParticipantes().add(nuevoParticipante);

        grupoRepo.save(grupo);
    }

    public List<Grupo> listarTodos() {
        return grupoRepo.findAll();
    }

    // ✅ Obtener participantes como lista de Strings (variable básica)
    public List<String> obtenerNombresParticipantes(Long grupoId) throws GrupoNotFoundException {
        Grupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado"));

        return grupo.getParticipantes().stream()
                .map(p -> p.getUsuario().getLogin())
                .collect(Collectors.toList());
    }
}
