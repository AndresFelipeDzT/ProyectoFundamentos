package com.ingesoft.redsocial.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import jakarta.transaction.Transactional;

import com.ingesoft.redsocial.excepciones.DuplicateUsuarioException;
import com.ingesoft.redsocial.excepciones.InvalidPasswordException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.excepciones.PasswordMismatchException;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarios;

    // CU01 - Registrar nuevo usuario
public void registrarNuevoUsuario(String login, String nombre, String password) throws DuplicateUsuarioException, InvalidPasswordException {
    if (usuarios.existsById(login)) {
        throw new DuplicateUsuarioException("Ya existe otro usuario con ese login");
    }

    // Validación de contraseña profesional
    if (password == null || !password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$")) {
        throw new InvalidPasswordException(
            "La contraseña debe tener al menos 8 caracteres, incluir un número y un carácter especial"
        );
    }

    Usuario usuario = new Usuario();
    usuario.setLogin(login);
    usuario.setNombre(nombre);
    usuario.setPassword(password); // ideal: cifrar aquí
    usuarios.save(usuario);
}


    // CU02 - Iniciar sesión
    public void iniciarSesion (
        String login,
        String password
    ) throws UsuarioNotFoundException, PasswordMismatchException {

        // 2. Sistema verifica que exista un usuario con ese login
        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("No existe un usuario con ese login");
        }

        // 4. Sistema verifica que el password coincida con el password de ese usuario
        Usuario usuario = usuarios.findById(login).get();
        // si el password no coincide, lanzar excepción
        if (!password.equals(usuario.getPassword())) {
            throw new PasswordMismatchException("No coincide el password");
        }

        // 5. Sistema establece el usuario actual

    }


    // CU03 - Buscar persona
    public List<Usuario> buscarPersona (
        String nombre
    ) throws UsuarioNotFoundException {
        
        // 2. Sistema busca usuarios con ese nombre
        List<Usuario> personas = usuarios.findByNombreContainingIgnoreCase(nombre);

        if (personas == null || personas.size() == 0) {
            throw new UsuarioNotFoundException("No se encuentran personas con ese nombre");
        }

        // 3. Sistema muestra el login y nombre de los usuarios 
        return personas;
    }


    // ---

    public List<Usuario> getPersonas () {
        return usuarios.findAll();
    }
}
