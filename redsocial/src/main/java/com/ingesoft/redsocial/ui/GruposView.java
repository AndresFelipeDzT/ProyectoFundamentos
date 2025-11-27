package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import java.util.List;

@Route("grupos")
public class GruposView extends VerticalLayout {

    SessionService session;
    NavegacionComponent nav;
    GrupoService grupoService;

    TextField nombreGrupo;
    TextField descripcion;
    Button crearGrupo;

    Grid<Grupo> tabla;

    public GruposView(SessionService session, NavegacionComponent nav, GrupoService grupoService) {
        this.session = session;
        this.nav = nav;
        this.grupoService = grupoService;

        UI.getCurrent().access(this::validarSesion);

        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setAlignItems(Alignment.CENTER);

        add(nav);

        nombreGrupo = new TextField("Nombre del grupo");
        nombreGrupo.setWidth("300px");
        descripcion = new TextField("Descripción");
        descripcion.setWidth("300px");

        crearGrupo = new Button("Crear Grupo", VaadinIcon.PLUS_CIRCLE.create(), e -> crear());
        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        crearGrupo.getStyle().set("border-radius", "10px");
        crearGrupo.setWidth("300px");

        tabla = new Grid<>(Grupo.class);
        tabla.removeAllColumns();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo");
        tabla.addColumn(Grupo::getDescripcion).setHeader("Descripción");
        tabla.addColumn(Grupo::getCantidadParticipantes).setHeader("Participantes");

        // Botón "Ver detalles / Añadir participante"
        tabla.addComponentColumn(grupo -> {
            Button detalle = new Button("Detalles", VaadinIcon.USERS.create());
            detalle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            detalle.addClickListener(e -> abrirModal(grupo));
            return detalle;
        }).setHeader("Acciones");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        cargar();
    }

    private void crear() {
        try {
            grupoService.crearGrupo(session.getLoginEnSesion(), nombreGrupo.getValue(), descripcion.getValue());
            Notification.show("Grupo creado exitosamente");
            nombreGrupo.clear();
            descripcion.clear();
            cargar();
        } catch (Exception ex) {
            Notification.show("No fue posible crear el grupo: " + ex.getMessage());
        }
    }

    private void cargar() {
        tabla.setItems(grupoService.listarTodos());
    }

    private void validarSesion() {
        if (session.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }

    // Modal flotante con lista de participantes y añadir
    private void abrirModal(Grupo grupo) {
        Dialog modal = new Dialog();
        modal.setWidth("500px");
        modal.setHeight("400px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        // Encabezado + cerrar
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.add(new Div() {{ setText(grupo.getNombreGrupo()); }});
        Button cerrar = new Button("X", e -> modal.close());
        header.add(cerrar);

        // Descripción
        Div desc = new Div();
        desc.setText(grupo.getDescripcion());

        // Lista de participantes
        VerticalLayout listaParticipantes = new VerticalLayout();
        listaParticipantes.setHeight("200px");
        listaParticipantes.getStyle().set("overflow", "auto");

        try {
            List<String> participantes = grupoService.obtenerNombresParticipantes(grupo.getId());
            participantes.forEach(p -> listaParticipantes.add(new Div() {{ setText(p); }}));
        } catch (Exception ex) {
            Notification.show("Error cargando participantes");
        }

        // Botón añadir participante (login de sesión)
        Button añadir = new Button("Añadir participante", e -> {
            try {
                grupoService.añadirParticipante(grupo, session.getLoginEnSesion());
                Notification.show("Te uniste al grupo " + grupo.getNombreGrupo());
                // refrescar lista
                listaParticipantes.removeAll();
                grupoService.obtenerNombresParticipantes(grupo.getId())
                        .forEach(p -> listaParticipantes.add(new Div() {{ setText(p); }}));
            } catch (Exception ex) {
                Notification.show(ex.getMessage());
            }
        });

        layout.add(header, desc, listaParticipantes, añadir);
        modal.add(layout);
        modal.open();
    }
}
