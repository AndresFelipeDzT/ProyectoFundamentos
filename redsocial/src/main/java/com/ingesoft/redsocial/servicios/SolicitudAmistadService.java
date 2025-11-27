package com.ingesoft.redsocial.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.excepciones.*;
import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.SolicitudAmistadRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SolicitudAmistadService {

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private SolicitudAmistadRepository solicitudes;

    // Enviar solicitud
    public void enviarSolicitudAmistad(String loginRemitente, String loginDestinatario)
            throws UsuarioNotFoundException, SelfInvitationException {

        if (!usuarios.existsById(loginRemitente))
            throw new UsuarioNotFoundException("No existe usuario con el login remitente");
        if (!usuarios.existsById(loginDestinatario))
            throw new UsuarioNotFoundException("No existe usuario con el login destinatario");
        if (loginRemitente.equals(loginDestinatario))
            throw new SelfInvitationException("No se puede invitar a si mismo");

        Usuario remitente = usuarios.findById(loginRemitente).get();
        Usuario destinatario = usuarios.findById(loginDestinatario).get();

        SolicitudAmistad solicitud = new SolicitudAmistad();
        solicitud.setRemitente(remitente);
        solicitud.setDestinatario(destinatario);
        solicitud.setFechaSolicitud(LocalDate.now());

        remitente.getSolicitudesEnviadas().add(solicitud);
        destinatario.getSolicitudesRecibidas().add(solicitud);

        solicitudes.save(solicitud);
        usuarios.save(remitente);
        usuarios.save(destinatario);
    }

    // Ver solicitudes pendientes
    public List<SolicitudAmistad> obtenerSolicitudesPendientes(String login) throws UsuarioNotFoundException {

        if (!usuarios.existsById(login))
            throw new UsuarioNotFoundException("No existe un usuario con ese login");

        Usuario destinatario = usuarios.findById(login).get();
        return solicitudes.findByDestinatarioAndAceptadoIsNull(destinatario);
    }

    // Responder solicitud
    public void responderSolicitud(String login, Long solicitudId, boolean aceptar)
            throws UsuarioNotFoundException, SolicitudNotFoundException, NotDestinatarioException {

        if (!usuarios.existsById(login))
            throw new UsuarioNotFoundException("No existe un usuario con ese login");

        SolicitudAmistad solicitud = solicitudes.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException("No existe esa solicitud de amistad"));

        if (solicitud.getDestinatario() == null || !login.equals(solicitud.getDestinatario().getLogin())) {
            throw new NotDestinatarioException("La solicitud no tiene al usuario como destinatario");
        }

        solicitud.setAceptado(aceptar);
        solicitud.setFechaRespuesta(LocalDate.now());
        solicitudes.save(solicitud);
    }

    // Obtener amigos
    public List<Usuario> obtenerAmigos(String login) throws UsuarioNotFoundException {
        if (!usuarios.existsById(login))
            throw new UsuarioNotFoundException("No existe un usuario con ese login");

        return solicitudes.findAmigosById(login);
    }
}
