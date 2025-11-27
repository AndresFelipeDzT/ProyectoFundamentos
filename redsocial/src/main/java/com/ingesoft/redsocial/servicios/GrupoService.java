package com.ingesoft.redsocial.servicios;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.GrupoExistenteException;
import com.ingesoft.redsocial.excepciones.GrupoNotFoundException;
import com.ingesoft.redsocial.excepciones.UsuarioAlreadyInGroupException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
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

        ParticipantesGrupo participanteCreador = new ParticipantesGrupo();
        participanteCreador.setUsuario(creador);
        participanteCreador.setGrupo(grupo);
        grupo.getParticipantes().add(participanteCreador);

        return grupoRepo.save(grupo);
    }

    // Añadir participante con validación
    public void añadirParticipante(Grupo grupo, String loginUsuario)
            throws UsuarioNotFoundException, UsuarioAlreadyInGroupException {

        Usuario usuario = usuarioRepo.findById(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        boolean yaParticipa = grupo.getParticipantes().stream()
                .anyMatch(p -> p.getUsuario().getLogin().equals(loginUsuario));

        if (yaParticipa) {
            throw new UsuarioAlreadyInGroupException("El usuario ya es miembro de este grupo");
        }

        ParticipantesGrupo nuevoParticipante = new ParticipantesGrupo();
        nuevoParticipante.setUsuario(usuario);
        nuevoParticipante.setGrupo(grupo);
        grupo.getParticipantes().add(nuevoParticipante);

        grupoRepo.save(grupo);
    }

    // Listar grupos (solo datos básicos)
    public List<Grupo> listarTodos() {
        return grupoRepo.findAll();
    }

    // Obtener nombres de participantes
    public List<String> obtenerNombresParticipantes(Long grupoId) throws GrupoNotFoundException {
        Grupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado"));
        return grupo.getNombresParticipantes();
    }
}
