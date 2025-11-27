package com.ingesoft.redsocial.ui;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

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
        tabla.addColumn(g -> g.getParticipantes().size()).setHeader("Miembros");

        // Botón Unirse dentro de la tabla
        tabla.addComponentColumn(g -> {
            Button unirse = new Button("Unirse");
            unirse.addClickListener(e -> {
                try {
                    grupoService.unirseAGrupo(session.getLoginEnSesion(), g.getId());
                    Notification.show("Te uniste al grupo '" + g.getNombreGrupo() + "'", 3000, Notification.Position.MIDDLE);
                    tabla.setItems(grupoService.listarTodos()); // recarga completa
                } catch (Exception ex) {
                    Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE);
                }
            });
            unirse.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return unirse;
        }).setHeader("Acción");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        cargar();
    }

    private void crear() {
        try {
            Grupo nuevo = grupoService.crearGrupo(
                session.getLoginEnSesion(),
                nombreGrupo.getValue(),
                descripcion.getValue()
            );

            Notification.show("Grupo '" + nuevo.getNombreGrupo() + "' creado exitosamente", 3000, Notification.Position.MIDDLE);

            nombreGrupo.clear();
            descripcion.clear();

            tabla.setItems(grupoService.listarTodos());
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
