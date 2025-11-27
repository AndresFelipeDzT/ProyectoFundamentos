package com.ingesoft.redsocial.modelo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

@Component
public class AppData {

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final File archivoGrupos = new File("grupos.json");
    private final File archivoUsuarios = new File("usuarios.json");

    public AppData() {
        cargarDatos();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    /**
     * Guardar cambios en JSON
     */
    public void guardarCambios() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivoGrupos, grupos);
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivoUsuarios, usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cargar datos desde archivos JSON
     */
    private void cargarDatos() {
        try {
            if (archivoGrupos.exists()) {
                grupos = mapper.readValue(archivoGrupos, new TypeReference<List<Grupo>>() {});
            }
            if (archivoUsuarios.exists()) {
                usuarios = mapper.readValue(archivoUsuarios, new TypeReference<List<Usuario>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtener participantes de un grupo por ID
     */
    public List<ParticipantesGrupo> getParticipantes(Long grupoId) {
        return grupos.stream()
                .filter(g -> g.getId().equals(grupoId))
                .findFirst()
                .map(Grupo::getParticipantes)
                .orElse(List.of());
    }
}
