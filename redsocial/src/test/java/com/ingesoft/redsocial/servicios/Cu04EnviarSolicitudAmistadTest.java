package com.ingesoft.redsocial.servicios;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.excepciones.SelfInvitationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.SolicitudAmistadRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@SpringBootTest
@Transactional
class Cu04EnviarSolicitudAmistadTest {

    @Autowired
    SolicitudAmistadService solicitudService;

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    SolicitudAmistadRepository solicitudes;

    @Test
    void enviarSolicitudConExito() throws Exception {
        usuarios.deleteAll();
        solicitudes.deleteAll();

        Usuario remitente = new Usuario();
        remitente.setLogin("alice");
        remitente.setNombre("Alice");
        remitente.setPassword("pwdalice");
        usuarios.save(remitente);

        Usuario destinatario = new Usuario();
        destinatario.setLogin("bob");
        destinatario.setNombre("Bob");
        destinatario.setPassword("pwdbob");
        usuarios.save(destinatario);

        assertDoesNotThrow(() -> solicitudService.enviarSolicitudAmistad("alice", "bob"));

        // Verificar que la solicitud quedó creada
        Usuario d = usuarios.findById("bob").orElseThrow();
        List<SolicitudAmistad> pendientes = solicitudes.findByDestinatarioAndAceptadoIsNull(d);
        assertEquals(1, pendientes.size());

        SolicitudAmistad s = pendientes.get(0);
        assertEquals("alice", s.getRemitente().getLogin());
        assertEquals("bob", s.getDestinatario().getLogin());

        // asociaciones bidireccionales guardadas
        Usuario r = usuarios.findById("alice").orElseThrow();
        assertTrue(r.getSolicitudesEnviadas().stream().anyMatch(x -> x.getDestinatario().getLogin().equals("bob")));
        assertTrue(d.getSolicitudesRecibidas().stream().anyMatch(x -> x.getRemitente().getLogin().equals("alice")));
    }

    @Test
    void fallaSiRemitenteNoExiste() {
        usuarios.deleteAll();
        solicitudes.deleteAll();

    assertThrows(UsuarioNotFoundException.class, () -> solicitudService.enviarSolicitudAmistad("noexiste", "bob"));
    }

    @Test
    void fallaSiDestinatarioNoExiste() {
        usuarios.deleteAll();
        solicitudes.deleteAll();

        Usuario remitente = new Usuario();
        remitente.setLogin("charlie");
        remitente.setNombre("Charlie");
        remitente.setPassword("pwdcharlie");
        usuarios.save(remitente);

    assertThrows(UsuarioNotFoundException.class, () -> solicitudService.enviarSolicitudAmistad("charlie", "noexiste"));
    }

    @Test
    void fallaSiSeInvitaASiMismo() throws Exception {
        usuarios.deleteAll();
        solicitudes.deleteAll();

        Usuario u = new Usuario();
        u.setLogin("daniel");
        u.setNombre("Daniel");
        u.setPassword("pwddaniel");
        usuarios.save(u);

    assertThrows(SelfInvitationException.class, () -> solicitudService.enviarSolicitudAmistad("daniel", "daniel"));
    }

}
