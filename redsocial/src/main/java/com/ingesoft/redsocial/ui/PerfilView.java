package com.ingesoft.redsocial.ui;

// Nuevas importaciones para estilo, icono y centrado
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.html.H3; // Cambiamos H1 por H3

import com.ingesoft.redsocial.modelo.PerfilAcademico;
import com.ingesoft.redsocial.servicios.PerfilAcademicoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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

        // ********** INICIO DE MODIFICACIONES DE ESTILO **********
        
        // 1. Aplicar gama de colores azul de fondo
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        setAlignItems(Alignment.CENTER); // Centrar el contenido horizontalmente
        setSpacing(true); // Agregar espacio entre componentes

        // 2. Encabezado con estilo azul
        H3 titulo = new H3("Mi Perfil Académico");
        titulo.getStyle().set("color", "#007BFF"); // Color azul primario

        add(navegacion, titulo);
        
        carrera = new TextField("Carrera");
        carrera.setWidth("350px");
        semestre = new TextField("Semestre");
        semestre.setWidth("350px");
        habilidades = new TextField("Habilidades principales");
        habilidades.setWidth("350px");

        cargarDatos();

        // 3. Botón Guardar con estilo azul, grande y redondeado
        guardar = new Button("Guardar Cambios");
        guardar.addClickListener(e -> guardarPerfil());
        guardar.addThemeVariants(
            ButtonVariant.LUMO_PRIMARY,
            ButtonVariant.LUMO_LARGE
        );
        guardar.getStyle().set("border-radius", "10px");
        guardar.setWidth("350px");

        add(carrera, semestre, habilidades, guardar);

        // ********** FIN DE MODIFICACIONES DE ESTILO **********
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
