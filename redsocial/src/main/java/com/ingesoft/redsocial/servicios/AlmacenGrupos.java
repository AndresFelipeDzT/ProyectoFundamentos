package com.ingesoft.redsocial.servicios;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingesoft.redsocial.modelo.Grupo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlmacenGrupos {

    private static final String RUTA_JSON = "grupos.json";
    private List<Grupo> grupos = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    public AlmacenGrupos() {
        cargarDatos();
    }

    // Cargar datos desde JSON
    private void cargarDatos() {
        File archivo = new File(RUTA_JSON);
        if (archivo.exists()) {
            try {
                grupos = mapper.readValue(archivo, new TypeReference<List<Grupo>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Guardar datos en JSON
    public void guardarDatos() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_JSON), grupos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void agregarGrupo(Grupo grupo) {
        grupos.add(grupo);
    }
}
