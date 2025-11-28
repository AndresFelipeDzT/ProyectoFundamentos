package com.ingesoft.redsocial.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.ComentarioRepository;
import com.ingesoft.redsocial.repositorios.PublicacionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComentarioService {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PublicacionRepository publicaciones;

    @Autowired
    private ComentarioRepository comentarios;

    // Crear comentario o respuesta
    public Comentario crearComentario(String login, Long idPublicacion, String texto, Long idPadre)
            throws UsuarioNotFoundException {

        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("Usuario no existe");
        }

        Usuario autor = usuarios.findById(login).get();
        Publicacion publicacion = publicaciones.findById(idPublicacion).orElseThrow();

        Comentario comentario = new Comentario();
        comentario.setAutor(autor);
        comentario.setPublicacion(publicacion);
        comentario.setTexto(texto);
        comentario.setFecha(LocalDateTime.now());

        if (idPadre != null) {
            Comentario padre = comentarios.findById(idPadre).orElseThrow();
            comentario.setComentarioPadre(padre);
        }

        return comentarios.save(comentario);
    }

    // Obtener solo comentarios principales (padre=null)
    public List<Comentario> obtenerComentarios(Long idPublicacion) {
        return comentarios.findByPublicacionIdAndComentarioPadreIsNullOrderByFechaAsc(idPublicacion);
    }
}
