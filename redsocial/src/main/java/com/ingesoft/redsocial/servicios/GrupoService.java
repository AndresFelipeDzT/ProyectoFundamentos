package com.ingesoft.redsocial.servicios;

import java.util.List;
import java.util.Random;

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

    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion) {
        Usuario creador = data.getUsuarios().stream()
            .filter(u -> u.getLogin().equals(loginCreador))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (data.getGrupos().stream().anyMatch(g -> g.getNombreGrupo().equalsIgnoreCase(nombre))) {
            throw new RuntimeException("Grupo ya existe");
        }

        Grupo grupo = new Grupo();
        grupo.setId(System.currentTimeMillis());
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(creador);
        participante.setGrupo(grupo);
        grupo.getParticipantes().add(participante);

        data.getGrupos().add(grupo);
        data.guardarCambios();

        return grupo;
    }

    public void unirseAGrupo(String login, Long idGrupo) {
        Usuario usuario = data.getUsuarios().stream()
            .filter(u -> u.getLogin().equals(login))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Grupo grupo = data.getGrupos().stream()
            .filter(g -> g.getId().equals(idGrupo))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        boolean yaParticipa = grupo.getParticipantes().stream()
            .anyMatch(p -> p.getUsuario().getLogin().equals(login));
        if (yaParticipa) throw new RuntimeException("Usuario ya pertenece");

        ParticipantesGrupo participante = new ParticipantesGrupo();
        participante.setUsuario(usuario);
        participante.setGrupo(grupo);
        grupo.getParticipantes().add(participante);

        data.guardarCambios();
    }
}
