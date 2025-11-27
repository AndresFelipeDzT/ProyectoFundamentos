package com.ingesoft.redsocial.servicios;

import org.springframework.stereotype.Service;
import com.ingesoft.redsocial.modelo.AppData;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.persistencia.JsonDatabase;

import java.util.List;

@Service
public class GrupoService {

    private AppData data;

    public GrupoService() {
        data = JsonDatabase.load();
    }

    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion) throws Exception {
        // Validar duplicado
        if (data.getGrupos().stream().anyMatch(g -> g.getNombreGrupo().equalsIgnoreCase(nombre))) {
            throw new Exception("Ya existe un grupo con ese nombre");
        }

        Usuario creador = data.getUsuarios().stream()
                .filter(u -> u.getLogin().equals(loginCreador))
                .findFirst()
                .orElseThrow(() -> new Exception("Usuario creador no existe"));

        Grupo grupo = new Grupo();
        grupo.setId(System.currentTimeMillis()); // id único
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        data.getGrupos().add(grupo);
        JsonDatabase.save(data);

        return grupo;
    }

    public void unirseAGrupo(String login, Long idGrupo) throws Exception {
        Usuario usuario = data.getUsuarios().stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Grupo grupo = data.getGrupos().stream()
                .filter(g -> g.getId().equals(idGrupo))
                .findFirst()
                .orElseThrow(() -> new Exception("Grupo no existe"));

        boolean yaPertenece = data.getParticipantes().stream()
                .anyMatch(p -> p.getUsuario().getLogin().equals(login) && p.getGrupo().getId().equals(idGrupo));

        if (yaPertenece) throw new Exception("El usuario ya pertenece al grupo");

        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(usuario);
        participante.setGrupo(grupo);

        data.getParticipantes().add(participante);
        JsonDatabase.save(data);
    }

    public List<Grupo> listarTodos() {
        return data.getGrupos();
    }

    public List<ParticipantesGrupo> obtenerParticipantes(Long grupoId) {
        return data.getParticipantes().stream()
                .filter(p -> p.getGrupo().getId().equals(grupoId))
                .toList();
    }

    public AppData getAppData() {
        return data;
    }
}
