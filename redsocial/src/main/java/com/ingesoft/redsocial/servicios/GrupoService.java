package com.ingesoft.redsocial.servicios;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.GrupoExistenteException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.GrupoRepository;
import com.ingesoft.redsocial.repositorios.ParticipantesGrupoRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
@Transactional
public class GrupoService {

    @Autowired
    private GrupoRepository grupos;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private ParticipantesGrupoRepository participaciones;

    // Crear un grupo
    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion) 
            throws UsuarioNotFoundException, GrupoExistenteException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del grupo no puede estar vacío");
        }

        if (!usuarios.existsById(loginCreador)) {
            throw new UsuarioNotFoundException("El usuario creador no existe");
        }

        // Verificar nombre duplicado
        if (grupos.findByNombreGrupo(nombre).isPresent()) {
            throw new GrupoExistenteException(nombre);
        }

        Usuario creador = usuarios.findById(loginCreador).get();

        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        return grupos.save(grupo);
    }

    // Listar todos los grupos
    public List<Grupo> listarTodos() {
        return grupos.findAll();
    }

    // Buscar grupos por nombre
    public List<Grupo> buscarPorNombre(String filtro) {
        return grupos.findByNombreGrupoContainingIgnoreCase(filtro);
    }

    // Unirse a un grupo
    public void unirseAGrupo(String login, Long idGrupo) throws UsuarioNotFoundException {

        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("Usuario no encontrado");
        }

        if (!grupos.existsById(idGrupo)) {
            throw new RuntimeException("Grupo no existe");
        }

        if (participaciones.existsByGrupoIdAndUsuarioLogin(idGrupo, login)) {
            throw new RuntimeException("El usuario ya pertenece al grupo");
        }

        Usuario usuario = usuarios.findById(login).get();
        Grupo grupo = grupos.findById(idGrupo).get();

        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(usuario);
        participante.setGrupo(grupo);

        participaciones.save(participante);
    }

    // Obtener participantes de un grupo
    public List<ParticipantesGrupo> obtenerParticipantes(Long grupoId) {
        return participaciones.findByGrupoId(grupoId);
    }
}
