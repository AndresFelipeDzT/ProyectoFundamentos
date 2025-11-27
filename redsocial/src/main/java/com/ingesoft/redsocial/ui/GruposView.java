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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private final SessionService session;
    private final NavegacionComponent nav;
    private final GrupoService grupoService;

    private TextField nombreGrupo;
    private TextField descripcion;
    private Button crearGrupo;

    private Grid<Grupo> tabla;

    public GruposView(SessionService session, NavegacionComponent nav, GrupoService grupoService) {

        this.session = session;
        this.nav = nav;
        this.grupoService = grupoService;

        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setAlignItems(Alignment.CENTER);

        add(nav);

        UI.getCurrent().access(this::validarSesion);

        construirFormulario();
        construirTabla();

        cargar();
    }

    // ------------------------ FORMULARIO ------------------------------------

    private void construirFormulario() {
        nombreGrupo = new TextField("Nombre del grupo");
        nombreGrupo.setWidth("300px");

        descripcion = new TextField("Descripción");
        descripcion.setWidth("300px");

        crearGrupo = new Button("Crear Grupo", VaadinIcon.PLUS.create(), e -> crear());
        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        crearGrupo.setWidth("300px");

        add(nombreGrupo, descripcion, crearGrupo);
    }

    // ------------------------ TABLA -----------------------------------------

    private void construirTabla() {

        tabla = new Grid<>(Grupo.class, false);

        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo");
        tabla.addColumn(Grupo::getDescripcion).setHeader("Descripción");
        tabla.addColumn(Grupo::getCantidadParticipantes).setHeader("Participantes");

        tabla.addComponentColumn(grupo -> {
            Button ver = new Button(VaadinIcon.EYE.create(), e -> abrirModalParticipantes(grupo));
            ver.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            return ver;
        }).setHeader("Ver");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(tablaContainer);
    }

    // ------------------------ CREAR GRUPO -----------------------------------

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

    // ------------------------ CARGAR LISTA -----------------------------------

    private void cargar() {
        tabla.setItems(grupoService.listar());
    }

    // ------------------------ MODAL -----------------------------------------

    private void abrirModalParticipantes(Grupo grupo) {

        Dialog dlg = new Dialog();
        dlg.setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);

        // Título
        layout.add("Participantes de: " + grupo.getNombreGrupo());

        // Lista de participantes
        List<String> participantes = grupoService.obtenerNombresParticipantes(grupo.getId());

        participantes.forEach(p -> layout.add("• " + p));

        // Botón para cerrar
        Button cerrar = new Button("Cerrar", e -> dlg.close());
        cerrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(cerrar);

        dlg.add(layout);
        dlg.open();
    }

    // ------------------------ SESIÓN -----------------------------------------

    private void validarSesion() {
        if (session.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
