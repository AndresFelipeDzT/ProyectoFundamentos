package com.ingesoft.redsocial.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.PublicacionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PublicacionService {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PublicacionRepository publicaciones;

    // Crear publicación con archivo opcional
    public Publicacion crearPublicacion(String login, String contenido, String rutaArchivo) 
            throws UsuarioNotFoundException {

        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("Usuario no existe");
        }

        Usuario autor = usuarios.findById(login).get();

        Publicacion p = new Publicacion();
        p.setAutor(autor);
        p.setContenido(contenido);
        p.setFechaCreacion(LocalDateTime.now());
        p.setRutaArchivo(rutaArchivo);

        return publicaciones.save(p);
    }

    public List<Publicacion> obtenerFeed() {
        return publicaciones.findAllByOrderByFechaCreacionDesc();
    }

    public List<Publicacion> obtenerPorUsuario(String login) {
        return publicaciones.findByAutorLogin(login);
    }

    @Transactional
    public Publicacion obtenerPorIdConComentarios(Long id) {
        Publicacion pub = publicaciones.findById(id)
        .orElseThrow(() -> new RuntimeException("No se encontró la publicación"));

        // Forzar carga de comentarios y sus relaciones
        pub.getComentarios().forEach(c -> {
            c.getReacciones().size();
            c.getRespuestas().size();
        });

        return pub;
    }
}
