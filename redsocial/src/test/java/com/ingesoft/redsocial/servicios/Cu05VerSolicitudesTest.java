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

@SpringBootTest
class Cu05VerSolicitudesTest {

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    SolicitudAmistadRepository solicitudes;

    @Autowired
    SolicitudAmistadService solicitudService;

    @Test
    void obtenerSolicitudesConResultados() throws Exception {

        solicitudes.deleteAll();
        usuarios.deleteAll();

        String login = "bob";
        Usuario destinatario = new Usuario();
        destinatario.setLogin(login);
        destinatario.setNombre("Bob");
        destinatario = usuarios.save(destinatario);

        Usuario remitente = new Usuario();
        remitente.setLogin("alice");
        remitente.setNombre("Alice");
        remitente = usuarios.save(remitente);

        SolicitudAmistad s = new SolicitudAmistad();
        s.setRemitente(remitente);
        s.setDestinatario(destinatario);
        s = solicitudes.save(s);

        List<SolicitudAmistad> resultado = solicitudService.obtenerSolicitudesPendientes(login);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("alice", resultado.get(0).getRemitente().getLogin());
    }

    @Test
    void obtenerSolicitudesFallaSiUsuarioNoExiste() {

        usuarios.deleteAll();

        String login = "noexiste";

    assertThrows(UsuarioNotFoundException.class, () -> solicitudService.obtenerSolicitudesPendientes(login));
    }

    @Test
    void obtenerSolicitudesDevuelveListaVaciaSiNoHayPendientes() throws Exception {

        solicitudes.deleteAll();
        usuarios.deleteAll();

        String login = "carla";
        Usuario destinatario = new Usuario();
        destinatario.setLogin(login);
        destinatario.setNombre("Carla");
        destinatario = usuarios.save(destinatario);

        List<SolicitudAmistad> resultado = solicitudService.obtenerSolicitudesPendientes(login);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

}
