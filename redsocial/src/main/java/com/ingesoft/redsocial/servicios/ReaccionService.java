package com.ingesoft.redsocial.servicios;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Reaccion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.ComentarioRepository;
import com.ingesoft.redsocial.repositorios.ReaccionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
public class ReaccionService {

    private final ReaccionRepository reaccionRepository;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ReaccionService(ReaccionRepository reaccionRepository,
                           ComentarioRepository comentarioRepository,
                           UsuarioRepository usuarioRepository) {
        this.reaccionRepository = reaccionRepository;
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void reaccionar(String loginUsuario, Long comentarioId, Reaccion.TipoReaccion tipo)
            throws UsuarioNotFoundException {
        Usuario autor = usuarioRepository.findByLogin(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        // Si ya reaccionó, actualizar
        Reaccion reaccion = reaccionRepository.findByComentarioAndAutor(comentario, autor)
                .orElse(new Reaccion());

        reaccion.setComentario(comentario);
        reaccion.setAutor(autor);
        reaccion.setTipo(tipo);

        reaccionRepository.save(reaccion);
    }
}
