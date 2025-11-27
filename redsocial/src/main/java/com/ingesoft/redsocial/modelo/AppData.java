package com.ingesoft.redsocial.modelo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class AppData {

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();
    private List<SolicitudAmistad> solicitudes = new ArrayList<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final File archivo = new File("appdata.json");

    public AppData() {
        cargarDatos();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public List<SolicitudAmistad> getSolicitudes() {
        return solicitudes;
    }

    public List<ParticipantesGrupo> getParticipantes(Long grupoId) {
        for (Grupo g : grupos) {
            if (g.getId().equals(grupoId)) {
                return g.getParticipantes();
            }
        }
        return new ArrayList<>();
    }

    private void cargarDatos() {
        try {
            if (archivo.exists()) {
                AppData data = mapper.readValue(archivo, AppData.class);
                this.usuarios = data.getUsuarios();
                this.grupos = data.getGrupos();
                this.solicitudes = data.getSolicitudes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarCambios() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
