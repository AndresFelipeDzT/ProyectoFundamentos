package com.ingesoft.redsocial.ui;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private final SessionService session;
    private final NavegacionComponent nav;
    private final GrupoService grupoService;

    private final TextField nombreGrupo;
    private final TextField descripcion;
    private final Button crearGrupo;

    private final Grid<Grupo> tabla;

    public GruposView(SessionService session, NavegacionComponent nav, GrupoService grupoService) {
        this.session = session;
        this.nav = nav;
        this.grupoService = grupoService;

        UI.getCurrent().access(this::validarSesion);

        // ********** ESTILO GENERAL **********
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        setAlignItems(Alignment.CENTER); // Centrar contenido

        add(nav);

        // Campos de creación de grupo
        nombreGrupo = new TextField("Nombre del grupo");
        nombreGrupo.setWidth("300px");
        descripcion = new TextField("Descripción");
        descripcion.setWidth("300px");

        crearGrupo = new Button("Crear Grupo", VaadinIcon.PLUS_CIRCLE.create(), e -> crear());
        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        crearGrupo.getStyle().set("border-radius", "10px");
        crearGrupo.setWidth("300px");

        // Tabla de grupos
        tabla = new Grid<>(Grupo.class, false);
        tabla.removeAllColumns();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo").setAutoWidth(true);
        tabla.addColumn(grupo -> grupo.getDescripcion()).setHeader("Descripción").setAutoWidth(true);
        tabla.addColumn(grupo -> grupo.getParticipantes().size())
             .setHeader("Miembros").setAutoWidth(true);

        // Botón "Unirse" para cada fila
        tabla.addComponentColumn(grupo -> {
            Button btnUnirse = new Button("Unirse", VaadinIcon.USER_CHECK.create());
            btnUnirse.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            btnUnirse.getStyle().set("border-radius", "8px");

            btnUnirse.addClickListener(e -> {
                try {
                    grupoService.unirseAGrupo(session.getLoginEnSesion(), grupo.getId());
                    Notification.show("Te uniste al grupo: " + grupo.getNombreGrupo(), 3000, Notification.Position.MIDDLE);
                    cargar(); // refresca la tabla con la actualización de miembros
                } catch (Exception ex) {
                    Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE);
                }
            });

            return btnUnirse;
        }).setHeader("Acción");

        // Contenedor horizontal para centrar la tabla
        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
        tabla.setWidthFull();

        // Añadir todos los componentes a la vista
        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        // Cargar datos iniciales
        cargar();
    }

    // =================== MÉTODOS ===================

    private void crear() {
        try {
            grupoService.crearGrupo(session.getLoginEnSesion(), nombreGrupo.getValue(), descripcion.getValue());
            Notification.show("Grupo creado exitosamente", 3000, Notification.Position.MIDDLE);
            nombreGrupo.clear();
            descripcion.clear();
            cargar();
        } catch (Exception ex) {
            Notification.show("No fue posible crear el grupo: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
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
}
