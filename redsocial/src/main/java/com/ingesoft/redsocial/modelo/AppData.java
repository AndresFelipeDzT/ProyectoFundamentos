package com.ingesoft.redsocial.modelo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AppData {

    private static final String ARCHIVO = "appdata.json";

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();

    private final ObjectMapper mapper;

    public AppData() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        cargar();
    }

    // Getters
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    // Guardar cambios en appdata.json
    public void guardarCambios() {
        try {
            mapper.writeValue(new File(ARCHIVO), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cargar datos de appdata.json
    private void cargar() {
        File file = new File(ARCHIVO);
        if (file.exists()) {
            try {
                AppData data = mapper.readValue(file, AppData.class);
                this.usuarios = data.getUsuarios();
                this.grupos = data.getGrupos();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Obtener participantes de un grupo
    public List<ParticipantesGrupo> getParticipantes(Long grupoId) {
        for (Grupo g : grupos) {
            if (g.getId().equals(grupoId)) {
                return g.getParticipantes();
            }
        }
        return new ArrayList<>();
    }
}
