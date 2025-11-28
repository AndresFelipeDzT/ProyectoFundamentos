package com.ingesoft.redsocial.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.ingesoft.redsocial.modelo.PerfilAcademico;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.PerfilAcademicoService;
import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;

@Route("perfil")
@PageTitle("Mi Perfil")
public class PerfilView extends VerticalLayout {

    private final SessionService sessionService;
    private final PerfilAcademicoService perfilService;
    private final UsuarioService usuarioService;

    private PerfilAcademico perfil;
    private Usuario usuario;

    // COMPONENTES
    Avatar avatar;
    Span lblUsuario;
    TextField nombre;
    TextField carrera;
    TextField semestre;
    TextField habilidades;

    Button btnEditar;
    Button btnGuardar;

    public PerfilView(SessionService sessionService,
                      NavegacionComponent navegacion,
                      PerfilAcademicoService perfilService,
                      UsuarioService usuarioService) {

        this.sessionService = sessionService;
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;

        // Validar sesión
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("background-color", "#E6F7FF");

        H3 titulo = new H3("Mi Perfil Académico");
        titulo.getStyle().set("color", "#007BFF");

        add(navegacion, titulo);

        cargarDatosUsuario();
        construirUI();
        ponerModoLectura();
    }

    // -------- CARGAR DATOS --------
    private void cargarDatosUsuario() {
        String login = sessionService.getLoginEnSesion();
        try {
            usuario = usuarioService.obtenerPorLogin(login);
            perfil = perfilService.obtenerPerfil(login);

            if (perfil == null) {
                perfil = new PerfilAcademico(); // vacío
            }

        } catch (Exception e) {
            Notification.show("Error cargando perfil: " + e.getMessage());
        }
    }

    // -------- CREAR UI --------
    private void construirUI() {

        // FOTO
        avatar = new Avatar(usuario.getNombre());
        avatar.setImage(null); 
        avatar.setColorIndex(4);
        avatar.setHeight("110px");
        avatar.setWidth("110px");

        // DATOS FIJOS
        lblUsuario = new Span("Usuario: " + usuario.getLogin());
        lblUsuario.getStyle().set("font-weight", "bold");

        nombre = new TextField("Nombre");
        nombre.setValue(usuario.getNombre());
        nombre.setWidth("350px");

        carrera = new TextField("Carrera");
        carrera.setValue(perfil.getCarrera() != null ? perfil.getCarrera() : "");
        carrera.setWidth("350px");

        semestre = new TextField("Semestre");
        semestre.setValue(perfil.getSemestre() != null ? perfil.getSemestre() : "");
        semestre.setWidth("350px");

        habilidades = new TextField("Habilidades");
        habilidades.setValue(perfil.getHabilidades() != null ? perfil.getHabilidades() : "");
        habilidades.setWidth("350px");

        // BOTÓN EDITAR
        btnEditar = new Button("Editar", new Icon(VaadinIcon.EDIT));
        btnEditar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEditar.getStyle().set("border-radius", "8px");
        btnEditar.addClickListener(e -> ponerModoEdicion());

        // BOTÓN GUARDAR
        btnGuardar = new Button("Guardar cambios", new Icon(VaadinIcon.CHECK));
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        btnGuardar.getStyle().set("border-radius", "8px");
        btnGuardar.setVisible(false);
        btnGuardar.addClickListener(e -> guardarCambios());

        // LAYOUT SUPERIOR (FOTO + INFORMACIÓN)
        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        header.setAlignItems(Alignment.CENTER);

        VerticalLayout datos = new VerticalLayout(lblUsuario, nombre);
        datos.setSpacing(false);

        header.add(avatar, datos, btnEditar);

        add(header, carrera, semestre, habilidades, btnGuardar);
    }

    // -------- MODO LECTURA --------
    private void ponerModoLectura() {
        nombre.setReadOnly(true);
        carrera.setReadOnly(true);
        semestre.setReadOnly(true);
        habilidades.setReadOnly(true);

        btnEditar.setVisible(true);
        btnGuardar.setVisible(false);
    }

    // -------- MODO EDICIÓN --------
    private void ponerModoEdicion() {
        nombre.setReadOnly(false);
        carrera.setReadOnly(false);
        semestre.setReadOnly(false);
        habilidades.setReadOnly(false);

        btnEditar.setVisible(false);
        btnGuardar.setVisible(true);
    }

    // -------- GUARDAR --------
    private void guardarCambios() {
        try {
            usuario.setNombre(nombre.getValue());
            usuarioService.actualizarNombre(usuario);

            perfilService.actualizarPerfil(
                    usuario.getLogin(),
                    carrera.getValue(),
                    semestre.getValue(),
                    habilidades.getValue()
            );

            ponerModoLectura();
            Notification.show("Perfil actualizado correctamente");

        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }
}
