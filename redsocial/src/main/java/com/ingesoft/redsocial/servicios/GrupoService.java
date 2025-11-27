package com.ingesoft.redsocial.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.GrupoExistenteException;
import com.ingesoft.redsocial.excepciones.GrupoNotFoundException;
import com.ingesoft.redsocial.excepciones.UsuarioAlreadyInGroupException;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.GrupoRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepo;
    private final UsuarioRepository usuarioRepo;

    public GrupoService(GrupoRepository grupoRepo, UsuarioRepository usuarioRepo) {
        this.grupoRepo = grupoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Crear grupo
    public Grupo crearGrupo(String loginCreador, String nombreGrupo, String descripcion)
            throws GrupoExistenteException {

        if (grupoRepo.findByNombreGrupo(nombreGrupo).isPresent()) {
            throw new GrupoExistenteException("Ya existe un grupo con ese nombre");
        }

        Usuario creador = usuarioRepo.findById(loginCreador)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(nombreGrupo);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);
        grupo.getParticipantes().add(creador);

        return grupoRepo.save(grupo);
    }

    // Listar todos los grupos
    public List<Grupo> listarTodos() {
        return grupoRepo.findAll();
    }

    // Unirse a grupo
    public void unirseAGrupo(String loginUsuario, Long grupoId)
            throws GrupoNotFoundException, UsuarioAlreadyInGroupException {

        Grupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado"));

        Usuario usuario = usuarioRepo.findById(loginUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (grupo.getParticipantes().contains(usuario)) {
            throw new UsuarioAlreadyInGroupException("Ya eres miembro de este grupo");
        }

        grupo.getParticipantes().add(usuario);
        grupoRepo.save(grupo);
    }
}
