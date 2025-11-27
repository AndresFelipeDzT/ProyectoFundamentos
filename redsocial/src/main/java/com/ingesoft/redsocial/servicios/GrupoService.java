package com.ingesoft.redsocial.servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.excepciones.GrupoExistenteException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.AppData;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;

@Service
public class GrupoService {

    private final AppData data;

    public GrupoService(AppData data) {
        this.data = data;
    }

    /**
     * Crear un grupo nuevo y agregar al creador como participante
     */
    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion)
            throws UsuarioNotFoundException, GrupoExistenteException {

        // Buscar el usuario creador
        Usuario creador = data.getUsuarios().stream()
                .filter(u -> u.getLogin().equals(loginCreador))
                .findFirst()
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        // Validar que no exista un grupo con el mismo nombre
        if (data.getGrupos().stream().anyMatch(g -> g.getNombreGrupo().equalsIgnoreCase(nombre))) {
            throw new GrupoExistenteException("Ya existe un grupo con ese nombre");
        }

        // Crear grupo
        Grupo grupo = new Grupo();
        grupo.setId(System.currentTimeMillis()); // ID único temporal
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        // Crear participante (el creador)
        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(creador);
        participante.setGrupo(grupo);
        grupo.getParticipantes().add(participante);

        // Agregar grupo a la lista de AppData y guardar cambios en JSON
        data.getGrupos().add(grupo);
        data.guardarCambios();

        return grupo;
    }

    /**
     * Permite que un usuario se una a un grupo existente
     */
    public void unirseAGrupo(String login, Long idGrupo) throws UsuarioNotFoundException {
        Usuario usuario = data.getUsuarios().stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Grupo grupo = data.getGrupos().stream()
                .filter(g -> g.getId().equals(idGrupo))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        boolean yaParticipa = grupo.getParticipantes().stream()
                .anyMatch(p -> p.getUsuario().getLogin().equals(login));

        if (yaParticipa) {
            throw new RuntimeException("El usuario ya pertenece al grupo");
        }

        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(usuario);
        participante.setGrupo(grupo);

        grupo.getParticipantes().add(participante);
        data.guardarCambios();
    }

    /**
     * Devuelve todos los grupos existentes
     */
    public List<Grupo> listarTodos() {
        return data.getGrupos();
    }

    /**
     * Devuelve los participantes de un grupo específico
     */
    public List<ParticipantesGrupo> obtenerParticipantes(Long grupoId) {
        return data.getParticipantes(grupoId);
    }
}
