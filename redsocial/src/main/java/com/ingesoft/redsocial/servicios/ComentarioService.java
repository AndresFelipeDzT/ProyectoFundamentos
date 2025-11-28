package com.ingesoft.redsocial.servicios;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.excepciones.ComentarioNotFoundException;
import com.ingesoft.redsocial.excepciones.PublicacionNotFoundException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.ComentarioRepository;
import com.ingesoft.redsocial.repositorios.PublicacionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepositorio;

    @Autowired
    private UsuarioRepository usuarioRepositorio;

    @Autowired
    private PublicacionRepository publicacionRepositorio;

    public Comentario crearComentario(String loginAutor, Long idPublicacion, String texto)
            throws UsuarioNotFoundException, PublicacionNotFoundException {

        Usuario autor = usuarioRepositorio.findByLogin(loginAutor)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Publicacion publicacion = publicacionRepositorio.findById(idPublicacion)
                .orElseThrow(() -> new PublicacionNotFoundException("Publicación no encontrada"));

        Comentario c = new Comentario();
        c.setAutor(autor);
        c.setTexto(texto);
        c.setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        c.setPublicacion(publicacion);

        return comentarioRepositorio.save(c);
    }

    public Comentario obtenerPorId(Long id) throws ComentarioNotFoundException {
        return comentarioRepositorio.findById(id)
                .orElseThrow(() -> new ComentarioNotFoundException("Comentario no encontrado"));
    }
}
