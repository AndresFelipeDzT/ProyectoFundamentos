package com.ingesoft.redsocial.persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingesoft.redsocial.modelo.AppData;

import java.io.File;

public class JsonDatabase {

    private static final String DB_FILE = "src/main/resources/data/redsocial.json";
    private static ObjectMapper mapper = new ObjectMapper();
    private static AppData appData = new AppData();

    // Cargar al iniciar
    public static AppData load() {
        try {
            File file = new File(DB_FILE);
            if (!file.exists()) {
                save(appData); // crear archivo vacío si no existe
            } else {
                appData = mapper.readValue(file, AppData.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appData;
    }

    // Guardar en cualquier momento
    public static void save(AppData data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(DB_FILE), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AppData getData() {
        return appData;
    }
}
