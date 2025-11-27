package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.dialog.Dialog;

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

        tabla = new Grid<>(Grupo.class, false);
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo");
        tabla.addColumn(Grupo::getDescripcion).setHeader("Descripción");
        tabla.addColumn(Grupo::getCantidadParticipantes).setHeader("Participantes");

        // Botón Añadir participante en cada fila
        tabla.addComponentColumn(gr -> {
            Button btn = new Button("Añadir", VaadinIcon.USER.create());
            btn.addClickListener(ev -> abrirModalParticipantes(gr));
            return btn;
        }).setHeader("Acción");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

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

    // Modal de participantes
    private void abrirModalParticipantes(Grupo grupo) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        Div titulo = new Div();
        titulo.setText("Grupo: " + grupo.getNombreGrupo());

        Div desc = new Div();
        desc.setText("Descripción: " + grupo.getDescripcion());

        Div listaParticipantes = new Div();
        listaParticipantes.getStyle().set("max-height", "200px");
        listaParticipantes.getStyle().set("overflow", "auto");
        try {
            List<String> participantes = grupoService.obtenerNombresParticipantes(grupo.getId());
            participantes.forEach(p -> listaParticipantes.add(new Div() {{ setText(p); }}));
        } catch (Exception e) {
            listaParticipantes.add(new Div() {{ setText("No se pudo cargar participantes"); }});
        }

        Button cerrar = new Button("X", e -> dialog.close());
        Button añadir = new Button("Añadir participante", VaadinIcon.USER.create(), e -> {
            // Lógica para añadir participante
        });

        VerticalLayout contenido = new VerticalLayout(titulo, desc, listaParticipantes, añadir, cerrar);
        dialog.add(contenido);
        dialog.open();
    }
}
