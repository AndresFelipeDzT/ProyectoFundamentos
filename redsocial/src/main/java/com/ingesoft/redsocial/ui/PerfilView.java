package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.PerfilAcademico;
import com.ingesoft.redsocial.servicios.PerfilAcademicoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    TextField habilidades;
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

        UI.getCurrent().access(this::validarSesion);

        add(navegacion, new H1("Perfil Académico"));

        carrera = new TextField("Carrera");
        semestre = new TextField("Semestre");
        habilidades = new TextField("Habilidades principales");

        cargarDatos();

        guardar = new Button("Guardar Cambios");
        guardar.addClickListener(e -> guardarPerfil());

        add(carrera, semestre, habilidades, guardar);
    }

    private void cargarDatos() {
        String login = sessionService.getLoginEnSesion();
        try {
            perfil = perfilService.obtenerPerfil(login);
            if (perfil != null) {
                carrera.setValue(perfil.getCarrera() != null ? perfil.getCarrera() : "");
                semestre.setValue(perfil.getSemestre() != null ? perfil.getSemestre() : "");
                habilidades.setValue(perfil.getHabilidades() != null ? perfil.getHabilidades() : "");
            }
        } catch (Exception e) {
            Notification.show("No hay perfil aún. Puedes crear uno.");
        }
    }

    private void guardarPerfil() {
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

