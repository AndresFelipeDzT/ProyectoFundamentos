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

    private final ObjectMapper mapper = new ObjectMapper();
    private final File usuariosFile = new File("usuarios.json");
    private final File gruposFile = new File("grupos.json");

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Grupo> grupos = new ArrayList<>();

    public AppData() {
        cargar();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void guardarCambios() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(usuariosFile, usuarios);
            mapper.writerWithDefaultPrettyPrinter().writeValue(gruposFile, grupos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargar() {
        try {
            if (usuariosFile.exists()) {
                usuarios = mapper.readValue(usuariosFile, new TypeReference<List<Usuario>>() {});
            }
            if (gruposFile.exists()) {
                grupos = mapper.readValue(gruposFile, new TypeReference<List<Grupo>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
