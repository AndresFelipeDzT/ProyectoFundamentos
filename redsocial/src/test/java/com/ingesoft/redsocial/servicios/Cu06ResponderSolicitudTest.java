package com.ingesoft.redsocial.servicios;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.excepciones.SolicitudNotFoundException;
import com.ingesoft.redsocial.excepciones.NotDestinatarioException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
class Cu06ResponderSolicitudTest {

    @Autowired
    SolicitudAmistadService solicitudService;

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    SolicitudAmistadRepository solicitudes;

    @Test
    void responderSolicitudAceptarConExito() throws Exception {
        usuarios.deleteAll();
        solicitudes.deleteAll();

        Usuario remitente = new Usuario();
        remitente.setLogin("u1");
        remitente.setNombre("Rem");
        remitente.setPassword("p1");
        remitente = usuarios.save(remitente);

        Usuario destinatario = new Usuario();
        destinatario.setLogin("u2");
        destinatario.setNombre("Dest");
        destinatario.setPassword("p2");
        destinatario = usuarios.save(destinatario);

        SolicitudAmistad s = new SolicitudAmistad();
        s.setRemitente(remitente);
        s.setDestinatario(destinatario);
        s = solicitudes.save(s);

        Long id = s.getId();

        assertDoesNotThrow(() -> solicitudService.responderSolicitud("u2", id, true));

        SolicitudAmistad updated = solicitudes.findById(id).orElseThrow();
        assertNotNull(updated.getFechaRespuesta());
        assertEquals(Boolean.TRUE, updated.getAceptado());
    }

    @Test
    void responderSolicitudFallaSiUsuarioNoExiste() {
        usuarios.deleteAll();
        solicitudes.deleteAll();

    assertThrows(UsuarioNotFoundException.class, () -> solicitudService.responderSolicitud("noex", 1L, true));
    }

    @Test
    void responderSolicitudFallaSiSolicitudNoExiste() throws Exception {
        usuarios.deleteAll();
        solicitudes.deleteAll();

        Usuario u = new Usuario();
        u.setLogin("abc");
        u.setNombre("Abc");
        u.setPassword("p");
        usuarios.save(u);

    assertThrows(SolicitudNotFoundException.class, () -> solicitudService.responderSolicitud("abc", 9999L, true));
    }

    @Test
    void responderSolicitudFallaSiNoEsDestinatario() throws Exception {
        usuarios.deleteAll();
        solicitudes.deleteAll();

        Usuario remitente = new Usuario();
        remitente.setLogin("r");
        remitente.setNombre("R");
        remitente.setPassword("p");
        remitente = usuarios.save(remitente);

        Usuario destinatario = new Usuario();
        destinatario.setLogin("d");
        destinatario.setNombre("D");
        destinatario.setPassword("p");
        destinatario = usuarios.save(destinatario);

        SolicitudAmistad s = new SolicitudAmistad();
        s.setRemitente(remitente);
        s.setDestinatario(destinatario);
        s = solicitudes.save(s);

        Long id = s.getId();

        // intentar responder con otro usuario (remitente) -> falla
        assertThrows(
            NotDestinatarioException.class, 
            () -> solicitudService.responderSolicitud("r", id, false));
    }

}
