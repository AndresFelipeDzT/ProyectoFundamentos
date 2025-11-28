package com.ingesoft.redsocial.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ComentarioRepository comentarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             PublicacionRepository publicacionRepository,
                             UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void crearComentario(String loginUsuario, Long publicacionId, String texto, Long comentarioPadreId)
            throws UsuarioNotFoundException, PublicacionNotFoundException {
        Usuario autor = usuarioRepository.findByLogin(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new PublicacionNotFoundException("Publicación no encontrada"));

        Comentario comentario = new Comentario();
        comentario.setAutor(autor);
        comentario.setPublicacion(publicacion);
        comentario.setTexto(texto);
        comentario.setFecha(LocalDateTime.now());

        if (comentarioPadreId != null) {
            comentarioRepository.findById(comentarioPadreId).ifPresent(comentario::setComentarioPadre);
        }

        comentarioRepository.save(comentario);
    }

     @Transactional(readOnly = true)
    public Comentario obtenerPorIdConRespuestas(Long id) throws ComentarioNotFoundException {
    Comentario c = comentarioRepository.findById(id)
                    .orElseThrow(() -> new ComentarioNotFoundException("Comentario no encontrado"));
    // Inicializar respuestas para evitar LazyInitializationException
    c.getRespuestas().size();
    return c;
}

}
