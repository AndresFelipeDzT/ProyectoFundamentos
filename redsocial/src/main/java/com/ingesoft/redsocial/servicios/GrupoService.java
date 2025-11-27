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

    public Grupo crearGrupo(String login, String nombre, String descripcion)
            throws UsuarioNotFoundException {

        Usuario creador = usuarioRepo.findById(login)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        // Agrega al creador como participante
        ParticipantesGrupo pg = new ParticipantesGrupo(creador, grupo);
        grupo.getParticipantes().add(pg);

        return grupoRepo.save(grupo);
    }

    public List<String> obtenerNombresParticipantes(Long grupoId) {
        Grupo g = grupoRepo.findById(grupoId).orElseThrow();

        return g.getParticipantes()
                .stream()
                .map(p -> p.getUsuario().getLogin())
                .toList();
    }

    public List<Grupo> listar() {
        return grupoRepo.findAll();
    }
}
