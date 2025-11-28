package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.PerfilAcademico;
import com.ingesoft.redsocial.servicios.PerfilAcademicoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("perfil")
@PageTitle("Perfil Académico")
public class PerfilView extends VerticalLayout {

    SessionService sessionService;
    NavegacionComponent navegacion;
    PerfilAcademicoService perfilService;

    TextField carrera;
    TextField semestre;
    TextArea habilidades;
    TextField usuarioNombre;

    Button guardar;
    PerfilAcademico perfil;

    public PerfilView(
        SessionService sessionService,
        NavegacionComponent navegacion,
        PerfilAcademicoService perfilService
    ) {

        this.sessionService = sessionService;
        this.navegacion = navegacion;
        this.perfilService = perfilService;

        validarSesion();

        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setAlignItems(Alignment.CENTER);

        H3 titulo = new H3("Mi Perfil Académico");
        titulo.getStyle().set("color", "#007BFF");

        add(navegacion, titulo);

        // Usuario NO editable
        usuarioNombre = new TextField("Usuario");
        usuarioNombre.setWidth("350px");
        usuarioNombre.setReadOnly(true);

        carrera = new TextField("Carrera");
        carrera.setWidth("350px");

        semestre = new TextField("Semestre");
        semestre.setWidth("350px");

        habilidades = new TextArea("Habilidades principales");
        habilidades.setWidth("350px");
        habilidades.setHeight("120px");

        cargarDatos();

        guardar = new Button("Guardar cambios", e -> guardarPerfil());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        guardar.getStyle().set("border-radius", "10px");
        guardar.setWidth("350px");

        add(usuarioNombre, carrera, semestre, habilidades, guardar);
    }

    private void cargarDatos() {
        String login = sessionService.getLoginEnSesion();
        usuarioNombre.setValue(login);

        try {
            perfil = perfilService.obtenerPerfil(login);

            carrera.setValue(perfil.getCarrera() != null ? perfil.getCarrera() : "");
            semestre.setValue(perfil.getSemestre() != null ? perfil.getSemestre() : "");
            habilidades.setValue(perfil.getHabilidades() != null ? perfil.getHabilidades() : "");

        } catch (Exception e) {
            Notification.show("No hay perfil aún. Puedes crear uno.");
        }
    }

    private void guardarPerfil() {

        if (carrera.isEmpty() || semestre.isEmpty()) {
            Notification.show("Carrera y semestre no pueden estar vacíos");
            return;
        }

        try {
            perfilService.actualizarPerfil(
                sessionService.getLoginEnSesion(),
                carrera.getValue(),
                semestre.getValue(),
                habilidades.getValue()
            );

            Notification.show("Perfil actualizado correctamente");

        } catch (Exception e) {
            Notification.show("Error guardando perfil: " + e.getMessage());
        }
    }

    private void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
