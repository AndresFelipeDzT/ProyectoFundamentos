package com.ingesoft.redsocial.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Reaccion;
import com.ingesoft.redsocial.modelo.Reaccion.TipoReaccion;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.ComentarioRepository;
import com.ingesoft.redsocial.repositorios.ReaccionRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReaccionService {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private ComentarioRepository comentarios;

    @Autowired
    private ReaccionRepository reacciones;

    public Reaccion reaccionar(String login, Long comentarioId, TipoReaccion tipo) throws UsuarioNotFoundException {

        if (!usuarios.existsById(login)) throw new UsuarioNotFoundException("Usuario no existe");

        Usuario usuario = usuarios.findById(login).get();
        Comentario comentario = comentarios.findById(comentarioId).orElseThrow();

        boolean yaReacciono = reacciones.existsByComentarioIdAndUsuarioLogin(comentarioId, login);
        if (yaReacciono) return null; // evita duplicados

        Reaccion reaccion = new Reaccion();
        reaccion.setUsuario(usuario);
        reaccion.setComentario(comentario);
        reaccion.setTipo(tipo);

        return reacciones.save(reaccion);
    }
}
