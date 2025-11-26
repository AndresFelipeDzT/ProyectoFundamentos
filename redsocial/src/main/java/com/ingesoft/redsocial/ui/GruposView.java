package com.ingesoft.redsocial.ui;

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

        add(nav);

        nombreGrupo = new TextField("Nombre del grupo");
        descripcion = new TextField("Descripción");

        crearGrupo = new Button("Crear Grupo", e -> crear());

        tabla = new Grid<>(Grupo.class);
        tabla.removeAllColumns();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo");

        add(nombreGrupo, descripcion, crearGrupo, tabla);

        cargar();
    }

    private void crear() {
        try {
            grupoService.crearGrupo(
                session.getLoginEnSesion(),
                nombreGrupo.getValue(),
                descripcion.getValue()
            );

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
}

