package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private final SessionService session;
    private final GrupoService grupoService;

    private final TextField nombreGrupo;
    private final TextField descripcion;
    private final Button crearGrupo;
    private final Grid<Grupo> tabla;

    public GruposView(SessionService session, GrupoService grupoService) {
        this.session = session;
        this.grupoService = grupoService;

        setSizeFull();

        nombreGrupo = new TextField("Nombre del grupo");
        descripcion = new TextField("Descripción");
        crearGrupo = new Button("Crear Grupo", e -> crear());
        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        tabla = new Grid<>(Grupo.class);
        tabla.removeAllColumns();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo");
        tabla.addColumn(g -> g.getParticipantes().size()).setHeader("Miembros");
        tabla.addComponentColumn(g -> {
            Button unirse = new Button("Unirse", e -> {
                try {
                    grupoService.unirseAGrupo(session.getLoginEnSesion(), g.getId());
                    Notification.show("Te uniste al grupo '" + g.getNombreGrupo() + "'");
                    tabla.setItems(grupoService.listarTodos());
                } catch (Exception ex) {
                    Notification.show(ex.getMessage());
                }
            });
            unirse.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return unirse;
        }).setHeader("Acción");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidthFull();
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
            tabla.setItems(grupoService.listarTodos());
        } catch (Exception e) {
            Notification.show(e.getMessage());
        }
    }

    private void cargar() {
        tabla.setItems(grupoService.listarTodos());
    }
}
