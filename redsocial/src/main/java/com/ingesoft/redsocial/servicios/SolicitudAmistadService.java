package com.ingesoft.redsocial.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.SolicitudAmistadRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import jakarta.transaction.Transactional;

import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.excepciones.SelfInvitationException;
import com.ingesoft.redsocial.excepciones.SolicitudNotFoundException;
import com.ingesoft.redsocial.excepciones.NotDestinatarioException;

@Service
@Transactional
public class SolicitudAmistadService {

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    SolicitudAmistadRepository solicitudes;


    // CU04 - Enviar solicitud de Amistad
    public void enviarSolicitudAmistad (
        String loginRemitente,
        String loginDestinatario
    ) throws UsuarioNotFoundException, SelfInvitationException {

        // 2. Sistema verifica que exista un usuario con el login del remitente
        if (!usuarios.existsById(loginRemitente)) {
            throw new UsuarioNotFoundException("No existe usuario con el login remitente");
        }

        // 4. Sistema verifica que exista un usuario con el login del destinatario
        if (!usuarios.existsById(loginDestinatario)) {
            throw new UsuarioNotFoundException("No existe usuario con el login destinatario");
        }

        // 5. Sistema verifica que el login del remitente sea diferente al login del destinatario
        if (loginRemitente.equals(loginDestinatario)) {
            throw new SelfInvitationException("No se puede invitar a si mismo");
        }

        // 6. Sistema crea una nueva solicitud de amistad, con la fecha actual, el usuario remitente y el usuario destinatario    
        
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


    // CU05 - Ver solicitudes de amistad
    public List<SolicitudAmistad> obtenerSolicitudesPendientes (
        String login
    ) throws UsuarioNotFoundException {

        // 2. Sistema verifica que exista un usuario con ese login
        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("No existe un usuario con ese login");
        }

        // 3. Sistema busca solicitudes de amistad que no hayan sido aceptadas donde el usuario sea destinatario
        Usuario destinatario = usuarios.findById(login).get();
        List<SolicitudAmistad> pendientes = solicitudes.findByDestinatarioAndAceptadoIsNull(destinatario);

        // 4. Sistema muestra login y nombre del remitente de las solicitudes de amistad encontradas
        return pendientes;

    }

    // CU06 - Responder solicitud de amistad
    public void responderSolicitud(
        String login,
        Long solicitudId,
        boolean aceptar
    ) throws UsuarioNotFoundException, SolicitudNotFoundException, NotDestinatarioException {

        // 2. Sistema verifica que exista un usuario con ese login
        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("No existe un usuario con ese login");
        }

        // 4. Sistema verifica que exista una solicitud con ese id
        var opt = solicitudes.findById(solicitudId);
        if (opt.isEmpty()) {
            throw new SolicitudNotFoundException("No existe esa solicitud de amistad");
        }

        SolicitudAmistad solicitud = opt.get();

        // 5. Sistema verifica que la solicitud tenga al usuario como destinatario
        if (solicitud.getDestinatario() == null || !login.equals(solicitud.getDestinatario().getLogin())) {
            throw new NotDestinatarioException("La solicitud no tiene al usuario como destinatario");
        }

        // 6. Sistema actualiza la solicitud colocando la respuesta y la fecha actual
        solicitud.setAceptado(aceptar);
        solicitud.setFechaRespuesta(java.time.LocalDate.now());
        solicitudes.save(solicitud);
    }

    // CU07 - Ver amigos
    public List<Usuario> obtenerAmigos (
        String login
    ) throws UsuarioNotFoundException {

        // 2. Sistema verifica que exista un usuario con ese login
        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("No existe un usuario con ese login");
        }

        // 3. Sistema busca los remitentes de las solicitudes de amistad enaviadas a ese usuario, que hayan sido aceptadas
        // 4. Sistema busca los destinatarios de las solicitudes enviadas por ese usuario que hayan sido aceptadas
        List<Usuario> amigos = solicitudes.findAmigosById(login);

        // 5. Sistema muestra login y nombre de los usuarios encontrados en las solicitudes aceptadas
        return amigos;
    }

}
