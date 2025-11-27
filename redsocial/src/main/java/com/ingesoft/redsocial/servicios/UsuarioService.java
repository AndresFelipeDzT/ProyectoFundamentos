package com.ingesoft.redsocial.servicios;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.DuplicateUsuarioException;
import com.ingesoft.redsocial.excepciones.InvalidPasswordException;
import com.ingesoft.redsocial.excepciones.PasswordMismatchException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Registrar usuario
    public void registrarNuevoUsuario(String login, String nombre, String password)
            throws DuplicateUsuarioException, InvalidPasswordException {

        if (usuarioRepo.existsById(login)) {
            throw new DuplicateUsuarioException("Ya existe un usuario con ese login");
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

        usuarioRepo.save(usuario);
    }

    // Iniciar sesión
    public void iniciarSesion(String login, String password)
            throws UsuarioNotFoundException, PasswordMismatchException {

        Usuario usuario = usuarioRepo.findById(login)
                .orElseThrow(() -> new UsuarioNotFoundException("No existe usuario con ese login"));

        if (!passwordEncoder.matches(password.trim(), usuario.getPassword())) {
            throw new PasswordMismatchException("Contraseña incorrecta");
        }
    }

    // Buscar personas por nombre
    public List<Usuario> buscarPersona(String nombre) throws UsuarioNotFoundException {
        List<Usuario> personas = usuarioRepo.findByNombreContainingIgnoreCase(nombre);
        if (personas.isEmpty()) {
            throw new UsuarioNotFoundException("No se encuentran personas con ese nombre");
        }
        return personas;
    }

    // Obtener todos los usuarios
    public List<Usuario> getUsuarios() {
        return usuarioRepo.findAll();
    }
}
