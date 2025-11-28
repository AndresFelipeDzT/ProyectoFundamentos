package com.ingesoft.redsocial.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.excepciones.PublicacionNotFoundException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.PublicacionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepositorio;

    @Autowired
    private UsuarioRepository usuarioRepositorio;

    public Publicacion crearPublicacion(String loginUsuario, String contenido, String rutaImagen)
            throws UsuarioNotFoundException {
        Usuario autor = usuarioRepositorio.findByLogin(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Publicacion p = new Publicacion();
        p.setAutor(autor);
        p.setContenido(contenido);
        p.setRutaImagen(rutaImagen);
        p.setFechaCreacion(LocalDateTime.now());

        return publicacionRepositorio.save(p);
    }

    public List<Publicacion> obtenerFeed() {
        return publicacionRepositorio.findAllByOrderByFechaCreacionDesc();
    }

    public Publicacion obtenerPorIdConComentarios(Long id) throws PublicacionNotFoundException {
        return publicacionRepositorio.findById(id)
                .orElseThrow(() -> new PublicacionNotFoundException("Publicación no encontrada"));
    }
}
