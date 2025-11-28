package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.notification.Notification;

@Route("amigos")
@PageTitle("Amigos")
public class AmigosView extends VerticalLayout {

    SessionService sessionService;
    NavegacionComponent navegacion;
    UsuarioService usuarioService;
    Usuario usuario;

    public AmigosView(SessionService sessionService,
                      NavegacionComponent navegacion,
                      UsuarioService usuarioService) {

        this.sessionService = sessionService;
        this.navegacion = navegacion;
        this.usuarioService = usuarioService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(navegacion);
        add(new H1("Mis Amigos"));

        // ❌ Ya NO validamos sesión aquí (se hacía con UI.access)
        //    Ahora se hace en onAttach()
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {

        String login = sessionService.getLoginEnSesion();

        if (login == null) {
            Notification.show("Debes iniciar sesión primero");
            UI.getCurrent().navigate("login");
            return;
        }

        try {
            usuario = usuarioService.obtenerPorLogin(login);
        } catch (Exception e) {
            Notification.show("Error cargando usuario: " + e.getMessage());
            UI.getCurrent().navigate("login");
            return;
        }

        // Si quieres cargar contenido dependiendo del usuario, lo haces aquí:
        // cargarAmigos();
    }
}
