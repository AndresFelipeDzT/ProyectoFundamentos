package com.ingesoft.redsocial.servicios;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.AppData;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.persistencia.JsonDatabase;


@Service
public class UsuarioService {

    private AppData data;

    public UsuarioService() {
        this.data = JsonDatabase.load(); // cargar datos al inicio
    }

    public void registrarNuevoUsuario(String login, String nombre, String password) throws Exception {
        if (data.getUsuarios().stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new Exception("Ya existe otro usuario con ese login");
        }

        if (password == null || !password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$")) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres, incluir un número y un carácter especial");
        }

        Usuario u = new Usuario(login, nombre, password);
        data.getUsuarios().add(u);
        JsonDatabase.save(data); // guardar inmediatamente
    }

    public void iniciarSesion(String login, String password) throws Exception {
        Usuario u = data.getUsuarios().stream().filter(x -> x.getLogin().equals(login)).findFirst().orElse(null);
        if (u == null) throw new Exception("No existe usuario con ese login");
        if (!u.getPassword().equals(password)) throw new Exception("Password incorrecta");
    }

    public AppData getAppData() {
        return data;
    }
}
