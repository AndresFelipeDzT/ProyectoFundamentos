package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.SolicitudAmistadService;
import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route(value = "amigos")
@PageTitle("Amigos")
@AnonymousAllowed
public class AmigosView extends VerticalLayout {

    // == Servicios de la aplicación

    SessionService sessionService;
    UsuarioService usuarioService;
    SolicitudAmistadService solicitudAmistadService;

    // == Componentes
    NavegacionComponent navegacion;
    TabSheet tabSheet;
    Grid<Usuario> tablaUsuarios;
    Grid<Usuario> tablaAmigos;
    Grid<SolicitudAmistad> tablaSolicitudes;
    Button enviarSolicitud;
    Button aceptarSolicitud;
    Button rechazarSolicitud;


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

        UI.getCurrent().access(this::validarSesion);

        // ********** MODIFICACIONES DE ESTILO **********
        
        // 1. Aplicar gama de colores azul de fondo
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        setPadding(true); 

        // Estilo de borde redondeado para los botones
        String borderRadius = "10px";

        // ********** FIN MODIFICACIONES DE ESTILO **********

        // Tab de Usuarios
        tablaUsuarios = new Grid<>(Usuario.class);
        tablaUsuarios.removeAllColumns();
        tablaUsuarios.addColumn(Usuario::getNombre).setHeader("Usuario");
        
        HorizontalLayout usuariosButtons = new HorizontalLayout();

        // 2. Botón Enviar Solicitud (Icono más estándar y redondeo)
        enviarSolicitud = new Button("Enviar Solicitud", VaadinIcon.PLUS_CIRCLE.create(), e -> enviarSolicitud());
        enviarSolicitud.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        enviarSolicitud.getStyle().set("border-radius", borderRadius); 
        
        usuariosButtons.add(enviarSolicitud);
        
        // Tab de Amigos
        tablaAmigos = new Grid<>(Usuario.class);
        tablaAmigos.removeAllColumns();
        tablaAmigos.addColumn(Usuario::getNombre).setHeader("Amigo");

        // Tab de Solicitudes
        tablaSolicitudes = new Grid<>(SolicitudAmistad.class);
        tablaSolicitudes.removeAllColumns();
        
        // 🛑 FIX: LOGIC - La llamada a getOrigen() en SolicitudAmistad no está definida.
        // Comentamos la línea problemática y usamos una columna temporal para que compile.
        // DEBES DESCOMENTAR Y CAMBIAR 'getId' por el método correcto (ej: s -> s.getUsuarioOrigen().getNombre()).
        // tablaSolicitudes.addColumn(s -> s.getOrigen().getNombre()).setHeader("Solicitante"); 
        tablaSolicitudes.addColumn(SolicitudAmistad::getId).setHeader("ID Solicitud (Temporal)"); // Usado temporalmente para que compile
        
        HorizontalLayout solicitudesButtons = new HorizontalLayout();
        
        // 3. Botón Aceptar Solicitud (Icono y redondeo)
        aceptarSolicitud = new Button("Aceptar", VaadinIcon.CHECK.create(), e -> aceptarSolicitud());
        aceptarSolicitud.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        aceptarSolicitud.getStyle().set("border-radius", borderRadius); 

        // 4. Botón Rechazar Solicitud (Icono, estilo ERROR y redondeo)
        rechazarSolicitud = new Button("Rechazar", VaadinIcon.CLOSE.create(), e -> rechazarSolicitud());
        rechazarSolicitud.addThemeVariants(ButtonVariant.LUMO_ERROR);
        rechazarSolicitud.getStyle().set("border-radius", borderRadius); 

        solicitudesButtons.add(aceptarSolicitud, rechazarSolicitud);

        // Configuración de pestañas
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
        
        tabSheet.addSelectedChangeListener(e -> {
            if (e.getSelectedTab().getLabel().equals("Usuarios")) {
                cargaUsuarios();
            } else if (e.getSelectedTab().getLabel().equals("Amigos")) {
                cargaAmigos();
            } else if (e.getSelectedTab().getLabel().equals("Solicitudes")) {
                cargaSolicitudes();
            }
        });


        add(navegacion, tabSheet);

        // carga el contenido de la primera pestaña
        cargaUsuarios();

    }

    // == Controladores 

    public void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }

    public void enviarSolicitud() {
        try {
            Usuario usuario = tablaUsuarios.getSelectedItems().iterator().next();
            
            // 🛑 FIX: LOGIC - La firma del método enviarSolicitud(String, String) no existe en tu SolicitudAmistadService.
            // DEBES VERIFICAR la firma de tu método y corregirla si es necesario.
            // Por ejemplo, si tu método se llama 'solicitarAmistad':
            // solicitudAmistadService.solicitarAmistad(sessionService.getLoginEnSesion(), usuario.getLogin());
            solicitudAmistadService.enviarSolicitud(
                sessionService.getLoginEnSesion(),
                usuario.getLogin()
            );
            
            Notification.show("Solicitud enviada a " + usuario.getNombre());
            cargaUsuarios();

        } catch (Exception e) {
            Notification.show("Error enviando solicitud: " + e.getMessage());
        }
    }

    public void aceptarSolicitud() {
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

    public void rechazarSolicitud() {
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


    // == Otros Métodos

    public void cargaUsuarios() {
        try {
            tablaUsuarios.setItems(usuarioService.getPersonas());
        } catch (Exception e) {
            Notification.show("Error cargando usuarios: " + e.getMessage());
        }    
    }

    public void cargaAmigos() {
        String login = sessionService.getLoginEnSesion();
        try {
            tablaAmigos.setItems(solicitudAmistadService.obtenerAmigos(login));
        } catch (Exception e) {
            Notification.show("Error cargando amigos del usuario " + login + ": " + e.getMessage());
        }    
    }

    public void cargaSolicitudes() {
        String login = sessionService.getLoginEnSesion();
        try {
            tablaSolicitudes.setItems(solicitudAmistadService.obtenerSolicitudesPendientes(login));
        } catch (Exception e) {
            Notification.show("Error cargando solicitudes del usuario " + login + ": " + e.getMessage());
        }    
    }

}
