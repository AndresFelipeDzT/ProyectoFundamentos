package com.ingesoft.redsocial.servicios;

import org.springframework.stereotype.Service;
import com.ingesoft.redsocial.modelo.AppData;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.persistencia.JsonDatabase;

import java.util.List;

@Service
public class UsuarioService {

    private AppData data;

    public UsuarioService() {
        data = JsonDatabase.load(); // cargar datos al iniciar
    }

    public void registrarNuevoUsuario(String login, String nombre, String password) throws Exception {
        // Validar duplicado
        if (data.getUsuarios().stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new Exception("Ya existe otro usuario con ese login");
        }

        // Validación profesional de contraseña
        if (password == null || !password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$")) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres, incluir un número y un carácter especial");
        }

        Usuario usuario = new Usuario(login, nombre, password);
        data.getUsuarios().add(usuario);
        JsonDatabase.save(data); // guardar cambios
    }

    public void iniciarSesion(String login, String password) throws Exception {
        Usuario u = data.getUsuarios().stream().filter(user -> user.getLogin().equals(login)).findFirst().orElse(null);
        if (u == null) throw new Exception("No existe usuario con ese login");
        if (!u.getPassword().equals(password)) throw new Exception("Password incorrecta");
    }

    public List<Usuario> getUsuarios() {
        return data.getUsuarios();
    }

    public AppData getAppData() {
        return data;
    }

    public List<Usuario> buscarPersona(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarPersona'");
    }
}
