package com.ingesoft.redsocial.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ingesoft.redsocial.excepciones.DuplicateUsuarioException;
import com.ingesoft.redsocial.excepciones.InvalidPasswordException;
import com.ingesoft.redsocial.excepciones.PasswordMismatchException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.AppData;
import com.ingesoft.redsocial.modelo.Usuario;

@Service
public class UsuarioService {

    private final AppData data;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(AppData data) {
        this.data = data;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Registrar nuevo usuario
    public void registrarNuevoUsuario(String login, String nombre, String password)
            throws DuplicateUsuarioException, InvalidPasswordException {

        if (data.getUsuarios().stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new DuplicateUsuarioException("Ya existe otro usuario con ese login");
        }

        if (password == null || !password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$")) {
            throw new InvalidPasswordException(
                    "La contraseña debe tener al menos 8 caracteres, incluir un número y un carácter especial"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login.trim());
        usuario.setNombre(nombre.trim());
        usuario.setPassword(passwordEncoder.encode(password.trim()));

        data.getUsuarios().add(usuario);
        data.guardarCambios();
    }

    // Iniciar sesión
    public void iniciarSesion(String login, String password)
            throws UsuarioNotFoundException, PasswordMismatchException {

        Usuario usuario = data.getUsuarios().stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst()
                .orElseThrow(() -> new UsuarioNotFoundException("No existe usuario con ese login"));

        if (!passwordEncoder.matches(password.trim(), usuario.getPassword())) {
            throw new PasswordMismatchException("Password incorrecta");
        }
    }

    // Buscar personas por nombre
    public List<Usuario> buscarPersona(String nombre) throws UsuarioNotFoundException {
        List<Usuario> personas = data.getUsuarios().stream()
                .filter(u -> u.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();

        if (personas.isEmpty()) {
            throw new UsuarioNotFoundException("No se encuentran personas con ese nombre");
        }

        return personas;
    }

    // Obtener todos los usuarios
    public List<Usuario> getUsuarios() {
        return data.getUsuarios();
    }
}
