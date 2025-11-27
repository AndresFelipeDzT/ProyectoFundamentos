package com.ingesoft.redsocial.servicios;

import java.util.List;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.AppData;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.persistencia.JsonDatabase;

@Service
public class GrupoService {

    private AppData data;

    public GrupoService() {
        data = JsonDatabase.load(); // carga todo al iniciar
    }

    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion) throws Exception {
        // Verificar duplicados
        if (data.getGrupos().stream().anyMatch(g -> g.getNombreGrupo().equalsIgnoreCase(nombre))) {
            throw new Exception("Ya existe un grupo con ese nombre");
        }

        // Obtener usuario creador
        Usuario creador = data.getUsuarios().stream()
                .filter(u -> u.getLogin().equals(loginCreador))
                .findFirst()
                .orElseThrow(() -> new Exception("Usuario creador no existe"));

        Grupo grupo = new Grupo();
        grupo.setId(System.currentTimeMillis()); // o cualquier id único
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        data.getGrupos().add(grupo);

        JsonDatabase.save(data); // guarda todos los datos inmediatamente
        return grupo;
    }

    public List<Grupo> listarTodos() {
        return data.getGrupos();
    }

    public List<Grupo> buscarPorNombre(String filtro) {
        return data.getGrupos().stream()
                .filter(g -> g.getNombreGrupo().toLowerCase().contains(filtro.toLowerCase()))
                .toList();
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

        // Verificar si ya pertenece
        if (data.getParticipantes().stream()
                .anyMatch(p -> p.getUsuario().getLogin().equals(login) && p.getGrupo().getId().equals(idGrupo))) {
            throw new Exception("El usuario ya pertenece al grupo");
        }

        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(usuario);
        participante.setGrupo(grupo);

        data.getParticipantes().add(participante);

        JsonDatabase.save(data); // actualizar archivo
    }

    public List<ParticipantesGrupo> obtenerParticipantes(Long grupoId) {
        return data.getParticipantes().stream()
                .filter(p -> p.getGrupo().getId().equals(grupoId))
                .toList();
    }
}
