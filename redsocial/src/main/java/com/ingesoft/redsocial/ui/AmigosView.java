package com.ingesoft.redsocial.ui;

import java.util.List;
import java.util.Optional;

import com.ingesoft.redsocial.modelo.SolicitudAmistad;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.SolicitudAmistadService;
import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
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
    // - Elementos de la pantalla

	NavegacionComponent navegacion;

    Button                  botonInvitar;
    Button                  botonAceptar;
    Button                  botonRechazar;

    TextField               filtroNombre;
    Button                  botonBuscar;

    Grid<Usuario>           tablaUsuarios;
    Grid<Usuario>           tablaAmigos;
    Grid<SolicitudAmistad>  tablaSolicitudes;

    // == Constructor
    // - Crea la pantalla

    public AmigosView(
        SessionService sessionService,
        UsuarioService usuarioService,
        SolicitudAmistadService solicitudAmistadService,
        NavegacionComponent navegacion
    ) {

        this.sessionService = sessionService;
        this.usuarioService = usuarioService;
        this.solicitudAmistadService = solicitudAmistadService;

		// al momento de cargar la pantalla
		UI.getCurrent().access(() -> {
			alInicio_RevisarSesion();
		});

        // == pantalla a mostrar

        // componente de navegación
        add(navegacion);

        // Tabs
        // ====
        var tabs = new TabSheet();
        tabs.setWidth("100%");
        tabs.getStyle().set("flex-grow", "1");

        // panel de Usuarios
        // =================
        VerticalLayout panelUsuarios = new VerticalLayout();
        var acciones = new HorizontalLayout();

        botonInvitar = new Button("Invitar");
        botonInvitar.setEnabled(false);
        botonInvitar.addClickListener(
            e -> alClicInvitar_InvitaUsuarioSeleccionado()
        );
        acciones.add(botonInvitar);

        filtroNombre = new TextField();
        botonBuscar = new Button("Buscar");
        botonBuscar.addClickListener(
            e ->  alClicBuscar_BuscarUsuario()
        );
        acciones.add(filtroNombre);
        acciones.add(botonBuscar);
        
        panelUsuarios.add(acciones);

        tablaUsuarios = new Grid<>(Usuario.class);
        tablaUsuarios.removeAllColumns();
        tablaUsuarios.addColumn("login");
        tablaUsuarios.addColumn("nombre");
        tablaUsuarios.addSelectionListener( selection -> {
            Optional<Usuario> optionalUsuario = selection.getFirstSelectedItem();
            if (optionalUsuario.isPresent())
                botonInvitar.setEnabled(true);
            else 
                botonInvitar.setEnabled(false);
        });

        panelUsuarios.add(tablaUsuarios);

        tabs.add("Usuarios", panelUsuarios);

        // Panel de amigos
        // ===============
        tablaAmigos = new Grid<>(Usuario.class);
        tablaAmigos.removeAllColumns();
        tablaAmigos.addColumn("login");
        tablaAmigos.addColumn("nombre");

        tabs.add("Amigos", tablaAmigos);

        // Panel de solicitudes
        // ====================
        var panelSolicitudes = new VerticalLayout();
        var accionesSolicitudes = new HorizontalLayout();

        botonAceptar = new Button("Aceptar");
        botonAceptar.addClickListener(
            e -> alClicAceptar_AceptaInvitacionSeleccionada()
        );
        accionesSolicitudes.add(botonAceptar);

        botonRechazar = new Button("Rechazar");
        botonRechazar.addClickListener(
            e -> alClicRechazar_RechazaInvitacionSeleccionada()
        );
        accionesSolicitudes.add(botonRechazar);

        panelSolicitudes.add(accionesSolicitudes);

        tablaSolicitudes = new Grid<>(SolicitudAmistad.class);
        tablaSolicitudes.removeAllColumns();
        tablaSolicitudes.addColumn("remitente.nombre")
            .setHeader("Nombre Remitente");
        tablaSolicitudes.addColumn("destinatario.nombre")
            .setHeader("Nombre Destinatario");

        tablaSolicitudes.addSelectionListener( selection -> {
            Optional<SolicitudAmistad> opcional = selection.getFirstSelectedItem();
            if (opcional.isPresent()) {
                botonAceptar.setEnabled(true);
                botonRechazar.setEnabled(true);
            } else {
                botonAceptar.setEnabled(true);
                botonRechazar.setEnabled(true);
            }
        });

        panelSolicitudes.add(tablaSolicitudes);
        tabs.add("Solicitudes", panelSolicitudes);

        add(tabs);

        // == carga datos en la pantalla

        cargaUsuarios();
        cargaAmigos();
        cargaSolicitudes();

    }

    // == Controladores / Eventos
    // - obtiene los datos de la solicitud de la pantalla
    // - invoca a los servicios / la lógica de negocio
    // - actualiza la pantalla

	public void alInicio_RevisarSesion() {
		// si no hay nadie en la sesión
		if (sessionService.getLoginEnSesion() == null) {
			// debe ir a la página de login
			UI.getCurrent().navigate("login");
		}
	}

    public void alClicBuscar_BuscarUsuario() {
        try {
            List<Usuario> usuarios = usuarioService.buscarPersona(filtroNombre.getValue());
            tablaUsuarios.setItems(usuarios);
        } catch (Exception e) {
            Notification.show("No se encuentran usuarios con el filtro: " + filtroNombre.getValue());
        }
    }

    public void alClicInvitar_InvitaUsuarioSeleccionado() {
        try {
            Usuario seleccionado = tablaUsuarios.getSelectedItems().iterator().next();
            solicitudAmistadService.enviarSolicitudAmistad(
                sessionService.getLoginEnSesion(), 
                seleccionado.getLogin());
            cargaSolicitudes();

        } catch (Exception e) {
            Notification.show("Error invitando al usuario: " + e.getMessage());
        }
    }

    public void alClicAceptar_AceptaInvitacionSeleccionada() {
        try {
            SolicitudAmistad solicitud = tablaSolicitudes.getSelectedItems().iterator().next();
            solicitudAmistadService.responderSolicitud(
                sessionService.getLoginEnSesion(),
                solicitud.getId(), 
                true);
            cargaAmigos();
            cargaSolicitudes();

        } catch (Exception e) {
            Notification.show("Error aceptando invitación: " + e.getMessage());
        }
    }

    public void alClicRechazar_RechazaInvitacionSeleccionada() {
        try {
            SolicitudAmistad solicitud = tablaSolicitudes.getSelectedItems().iterator().next();
            solicitudAmistadService.responderSolicitud(
                sessionService.getLoginEnSesion(),
                solicitud.getId(), 
                false);
            cargaAmigos();
            cargaSolicitudes();

        } catch (Exception e) {
            Notification.show("Error rechazando invitación: " + e.getMessage());
        }
    }


    // == Otros Métodos
    // - para invocar la lógica de negocio más fácil

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
