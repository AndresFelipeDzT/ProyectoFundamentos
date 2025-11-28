package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.SolicitudAmistadService;
import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "amigos")
@PageTitle("Amigos")
@PermitAll
public class AmigosView extends VerticalLayout {

    // == Servicios
    private final SessionService sessionService;
    private final UsuarioService usuarioService;
    private final SolicitudAmistadService solicitudAmistadService;

    // == Componentes
    private final NavegacionComponent navegacion;
    private final TabSheet tabSheet;
    private final Grid<Usuario> tablaUsuarios;
    private final Grid<Usuario> tablaAmigos;
    private final Grid<SolicitudAmistad> tablaSolicitudes;
    private final Button enviarSolicitud;
    private final Button aceptarSolicitud;
    private final Button rechazarSolicitud;

    // == Constructor
    public AmigosView(
            SessionService sessionService,
            UsuarioService usuarioService,
            SolicitudAmistadService solicitudAmistadService,
            NavegacionComponent navegacion
    ) {
        this.sessionService = sessionService;
        this.usuarioService = usuarioService;
        this.solicitudAmistadService = solicitudAmistadService;
        this.navegacion = navegacion;

        // Estilo general
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setPadding(true);

        String borderRadius = "10px";

        // --- Grid de Usuarios ---
        tablaUsuarios = new Grid<>(Usuario.class);
        tablaUsuarios.removeAllColumns();
        tablaUsuarios.addColumn(Usuario::getNombre).setHeader("Usuario");

        HorizontalLayout usuariosButtons = new HorizontalLayout();
        enviarSolicitud = new Button("Enviar Solicitud", VaadinIcon.PLUS_CIRCLE.create(), e -> enviarSolicitud());
        enviarSolicitud.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        enviarSolicitud.getStyle().set("border-radius", borderRadius);
        usuariosButtons.add(enviarSolicitud);

        // --- Grid de Amigos ---
        tablaAmigos = new Grid<>(Usuario.class);
        tablaAmigos.removeAllColumns();
        tablaAmigos.addColumn(Usuario::getNombre).setHeader("Amigo");

        // --- Grid de Solicitudes ---
        tablaSolicitudes = new Grid<>(SolicitudAmistad.class);
        tablaSolicitudes.removeAllColumns();
        tablaSolicitudes.addColumn(SolicitudAmistad::getId).setHeader("ID Solicitud (Temporal)");

        HorizontalLayout solicitudesButtons = new HorizontalLayout();
        aceptarSolicitud = new Button("Aceptar", VaadinIcon.CHECK.create(), e -> aceptarSolicitud());
        aceptarSolicitud.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        aceptarSolicitud.getStyle().set("border-radius", borderRadius);

        rechazarSolicitud = new Button("Rechazar", VaadinIcon.CLOSE.create(), e -> rechazarSolicitud());
        rechazarSolicitud.addThemeVariants(ButtonVariant.LUMO_ERROR);
        rechazarSolicitud.getStyle().set("border-radius", borderRadius);

        solicitudesButtons.add(aceptarSolicitud, rechazarSolicitud);

        // --- TabSheet ---
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        VerticalLayout usuariosLayout = new VerticalLayout(tablaUsuarios, usuariosButtons);
        usuariosLayout.setPadding(true);
        usuariosLayout.setSpacing(true);
        tabSheet.add("Usuarios", usuariosLayout);

        VerticalLayout amigosLayout = new VerticalLayout(tablaAmigos);
        amigosLayout.setPadding(true);
        amigosLayout.setSpacing(true);
        tabSheet.add("Amigos", amigosLayout);

        VerticalLayout solicitudesLayout = new VerticalLayout(tablaSolicitudes, solicitudesButtons);
        solicitudesLayout.setPadding(true);
        solicitudesLayout.setSpacing(true);
        tabSheet.add("Solicitudes", solicitudesLayout);

        // Cambios de pestañas
        tabSheet.addSelectedChangeListener(e -> {
            String label = e.getSelectedTab().getLabel();
            if ("Usuarios".equals(label)) cargaUsuarios();
            else if ("Amigos".equals(label)) cargaAmigos();
            else if ("Solicitudes".equals(label)) cargaSolicitudes();
        });

        add(navegacion, tabSheet);
    }

    // == VALIDACIÓN DE SESIÓN
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
        cargaUsuarios();
    }

    private void enviarSolicitud() {
        try {
            Usuario usuario = tablaUsuarios.getSelectedItems().iterator().next();
            solicitudAmistadService.enviarSolicitudAmistad(
                    sessionService.getLoginEnSesion(),
                    usuario.getLogin()
            );
            Notification.show("Solicitud enviada a " + usuario.getNombre());
            cargaUsuarios();
        } catch (Exception e) {
            Notification.show("Error enviando solicitud: " + e.getMessage());
        }
    }

    private void aceptarSolicitud() {
        try {
            SolicitudAmistad solicitud = tablaSolicitudes.getSelectedItems().iterator().next();
            solicitudAmistadService.responderSolicitud(
                    sessionService.getLoginEnSesion(),
                    solicitud.getId(),
                    true
            );
            cargaAmigos();
            cargaSolicitudes();
        } catch (Exception e) {
            Notification.show("Error aceptando invitación: " + e.getMessage());
        }
    }

    private void rechazarSolicitud() {
        try {
            SolicitudAmistad solicitud = tablaSolicitudes.getSelectedItems().iterator().next();
            solicitudAmistadService.responderSolicitud(
                    sessionService.getLoginEnSesion(),
                    solicitud.getId(),
                    false
            );
            cargaAmigos();
            cargaSolicitudes();
        } catch (Exception e) {
            Notification.show("Error rechazando invitación: " + e.getMessage());
        }
    }

    // == Carga de datos
    private void cargaUsuarios() {
        try {
            tablaUsuarios.setItems(usuarioService.getUsuarios());
        } catch (Exception e) {
            Notification.show("Error cargando usuarios: " + e.getMessage());
        }
    }

    private void cargaAmigos() {
        try {
            tablaAmigos.setItems(solicitudAmistadService.obtenerAmigos(sessionService.getLoginEnSesion()));
        } catch (Exception e) {
            Notification.show("Error cargando amigos: " + e.getMessage());
        }
    }

    private void cargaSolicitudes() {
        try {
            tablaSolicitudes.setItems(solicitudAmistadService.obtenerSolicitudesPendientes(sessionService.getLoginEnSesion()));
        } catch (Exception e) {
            Notification.show("Error cargando solicitudes: " + e.getMessage());
        }
    }
}
