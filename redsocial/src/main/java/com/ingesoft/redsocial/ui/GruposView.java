package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
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

        // ********** ESTÉTICA **********
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
        tabla.addColumn(g -> g.getParticipantes().size()).setHeader("Miembros");

        // Columna con botón “Añadir”
        tabla.addComponentColumn(g -> {
            Button btnAñadir = new Button("Añadir", VaadinIcon.PLUS.create());
            btnAñadir.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            btnAñadir.addClickListener(ev -> mostrarDialogoGrupo(g));
            return btnAñadir;
        }).setHeader("Acción");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        cargar();
    }

    private void mostrarDialogoGrupo(Grupo grupo) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("400px");
        dialog.getElement().getStyle().set("padding", "20px");

        // Encabezado con nombre y X
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H3 titulo = new H3(grupo.getNombreGrupo());
        Button cerrar = new Button(VaadinIcon.CLOSE.create(), e -> dialog.close());

        header.add(titulo, cerrar);

        // Descripción
        Label desc = new Label(grupo.getDescripcion());
        desc.getStyle().set("font-size", "14px");

        // Lista de miembros
        Grid<Usuario> miembrosGrid = new Grid<>(Usuario.class);
        miembrosGrid.setItems(grupo.getParticipantes().stream().map(p -> p.getUsuario()).toList());
        miembrosGrid.removeAllColumns();
        miembrosGrid.addColumn(Usuario::getLogin).setHeader("Login");
        miembrosGrid.addColumn(Usuario::getNombre).setHeader("Nombre");
        miembrosGrid.setHeight("200px");

        VerticalLayout miembrosLayout = new VerticalLayout(new Label("Miembros"), miembrosGrid);
        miembrosLayout.setWidth("250px");

        // Botón Confirmar unión
        Button unirse = new Button("Unirse al grupo", e -> {
            try {
                grupoService.unirseAGrupo(session.getLoginEnSesion(), grupo.getId());
                Notification.show("Te uniste al grupo '" + grupo.getNombreGrupo() + "'");
                cargar();
                dialog.close();
            } catch (Exception ex) {
                Notification.show("No se pudo unir: " + ex.getMessage());
            }
        });
        unirse.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        unirse.setWidthFull();

        HorizontalLayout contenido = new HorizontalLayout(desc, miembrosLayout);
        contenido.setWidthFull();
        contenido.setJustifyContentMode(JustifyContentMode.BETWEEN);

        VerticalLayout layoutDialog = new VerticalLayout(header, contenido, unirse);
        layoutDialog.setSizeFull();
        layoutDialog.setAlignItems(Alignment.STRETCH);

        dialog.add(layoutDialog);
        dialog.open();
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
}
