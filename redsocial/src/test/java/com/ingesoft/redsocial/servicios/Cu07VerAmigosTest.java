package com.ingesoft.redsocial.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.SolicitudAmistadRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class Cu07VerAmigosTest {

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    SolicitudAmistadRepository solicitudes;

    @Autowired
    SolicitudAmistadService solicitudService;

    @Test
    void obtenerAmigosConResultados() throws Exception {

        usuarios.deleteAll();

        String login = "alice";

        Usuario usuario = new Usuario();
        usuario.setLogin("alice");
        usuario.setNombre("alice");
        usuario = usuarios.save(usuario);

        Usuario amigo1 = new Usuario();
        amigo1.setLogin("bob");
        amigo1.setNombre("Bob");
        amigo1 = usuarios.save(amigo1);

        SolicitudAmistad s = new SolicitudAmistad();
        s.setRemitente(usuario);
        s.setDestinatario(amigo1);
        s.setAceptado(true);
        s = solicitudes.save(s);

        Usuario amigo2 = new Usuario();
        amigo2.setLogin("carla");
        amigo2.setNombre("Carla");
        amigo2 = usuarios.save(amigo2);

        SolicitudAmistad s2 = new SolicitudAmistad();
        s2.setRemitente(amigo2);
        s2.setDestinatario(usuario);
        s2.setAceptado(true);
        s2 = solicitudes.save(s2);

        List<Usuario> amigos = solicitudService.obtenerAmigos(login);

        assertNotNull(amigos);
        assertEquals(2, amigos.size());
        assertEquals("bob", amigos.get(0).getLogin());
    }

    @Test
    void obtenerAmigosFallaSiUsuarioNoExiste() {

        usuarios.deleteAll();

        String login = "noexiste";

        assertThrows(UsuarioNotFoundException.class, () -> solicitudService.obtenerAmigos(login));
    }

    @Test
    void obtenerAmigosDevuelveListaVaciaSiNoHay() throws Exception {

        String login = "bob";

        usuarios.deleteAll();

        Usuario amigo1 = new Usuario();
        amigo1.setLogin("bob");
        amigo1.setNombre("Bob");
        amigo1 = usuarios.save(amigo1);
        
        List<Usuario> amigos = solicitudService.obtenerAmigos(login);

        assertNotNull(amigos);
        assertEquals(0, amigos.size());
    }

}
