package com.ingesoft.redsocial.ui;

import java.util.List;

import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.servicios.PublicacionService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("publicaciones")
@PageTitle("Publicaciones")
public class PublicacionesView extends VerticalLayout {

    SessionService sessionService;
    NavegacionComponent navegacion;
    PublicacionService publicacionService;

    TextArea areaPublicacion;
    Grid<Publicacion> tabla;

    public PublicacionesView(
        SessionService sessionService,
        NavegacionComponent navegacion,
        PublicacionService publicacionService
    ) {
        this.sessionService = sessionService;
        this.navegacion = navegacion;
        this.publicacionService = publicacionService;

        UI.getCurrent().access(this::validarSesion);

        add(navegacion);

        areaPublicacion = new TextArea("Nueva publicación");
        areaPublicacion.setWidthFull();

        var publicar = new com.vaadin.flow.component.button.Button("Publicar", e -> publicar());

        tabla = new Grid<>(Publicacion.class);
        tabla.removeAllColumns();
        tabla.addColumn(p -> p.getAutor().getNombre()).setHeader("Autor");
        tabla.addColumn(Publicacion::getContenido).setHeader("Contenido");
        tabla.addColumn(Publicacion::getFechaCreacion).setHeader("Fecha");

        add(areaPublicacion, publicar, tabla);

        cargarFeed();
    }

    private void publicar() {
        if (areaPublicacion.isEmpty()) {
            Notification.show("Debes escribir algo");
            return;
        }

        try {
            publicacionService.crearPublicacion(sessionService.getLoginEnSesion(), areaPublicacion.getValue());
            areaPublicacion.clear();
            cargarFeed();
        } catch (Exception e) {
            Notification.show("Error publicando: " + e.getMessage());
        }
    }

    private void cargarFeed() {
        List<Publicacion> publicaciones = publicacionService.obtenerFeed();
        tabla.setItems(publicaciones);
    }

    private void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}


