package com.ingesoft.redsocial.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.PublicacionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    public PublicacionService(PublicacionRepository publicacionRepository,
                              UsuarioRepository usuarioRepository) {
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Publicacion> obtenerFeed() {
        return publicacionRepository.findAllWithComentariosYAutor();
    }

    @Transactional(readOnly = true)
    public Publicacion obtenerPorIdConComentarios(Long id) {
        return publicacionRepository.findByIdWithComentarios(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
    }

    @Transactional
    public void crearPublicacion(String loginUsuario, String contenido, String rutaArchivo) throws UsuarioNotFoundException {
        Usuario autor = usuarioRepository.findByLogin(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Publicacion p = new Publicacion();
        p.setAutor(autor);
        p.setContenido(contenido);
        p.setFechaCreacion(LocalDateTime.now());
        p.setRutaArchivo(rutaArchivo);

        publicacionRepository.save(p);
    }
    
    public List<Publicacion> obtenerFeedConComentariosYReacciones() {
        return publicacionRepository.findAllWithComentariosYReacciones();
    }
}
