package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.TituloComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends Main {

    // Servicios
    private final SessionService session;
    private final UsuarioService usuarioService;
    private final TituloComponent tituloComponent;

    // Componentes
    private final LoginForm loginForm;
    private final VerticalLayout registroLayout = new VerticalLayout();
    private final VerticalLayout mainLayout = new VerticalLayout();

    public LoginView(SessionService session, UsuarioService usuarioService, TituloComponent tituloComponent) {
        this.session = session;
        this.usuarioService = usuarioService;
        this.tituloComponent = tituloComponent;

        // Layout principal centrado
        mainLayout.setSizeFull();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.getStyle().set("background-color", "#f0f4f8"); // fondo suave

        // Título
        tituloComponent.getStyle().set("font-size", "36px");
        tituloComponent.getStyle().set("color", "#1a73e8");
        mainLayout.add(tituloComponent);

        // LoginForm
        loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle().set("width", "350px");
        mainLayout.add(loginForm);

        // Botón para mostrar formulario de registro
        Button botonRegistrar = new Button("Registrar");
        botonRegistrar.getStyle().set("background-color", "#1a73e8");
        botonRegistrar.getStyle().set("color", "white");
        botonRegistrar.getStyle().set("margin-top", "10px");
        mainLayout.add(botonRegistrar);

        // Layout para el registro (inicialmente vacío)
        registroLayout.setWidth("350px");
        mainLayout.add(registroLayout);

        // Listeners
        botonRegistrar.addClickListener(event -> mostrarFormularioRegistro());
        loginForm.addLoginListener(event -> validaInicioSesion(event.getUsername(), event.getPassword()));

        // Agregamos el layout principal al Main
        add(mainLayout);
    }

    // === Login
    private void validaInicioSesion(String username, String password) {
        if (authenticate(username, password)) {
            Notification.show("Inicio de sesión correcto para " + username);
            session.setLoginEnSesion(username);
            UI.getCurrent().navigate(""); // navega a la página principal
        } else {
            loginForm.setError(true);
            Notification.show("Error iniciando sesión", 3000, Notification.Position.MIDDLE);
        }
    }

    private boolean authenticate(String login, String password) {
        try {
            usuarioService.iniciarSesion(login, password);
            return true;
        } catch (Exception e) {
            Notification.show("Error iniciando sesión: " + e.getMessage());
            return false;
        }
    }

    // === Registro
    private void mostrarFormularioRegistro() {
        registroLayout.removeAll(); // limpiar si ya hay algo

        TextField nombreField = new TextField("Nombre completo");
        TextField loginField = new TextField("Nombre de usuario");
        PasswordField passwordField = new PasswordField("Contraseña");

        Button enviar = new Button("Registrar");
        enviar.getStyle().set("background-color", "#1a73e8");
        enviar.getStyle().set("color", "white");
        enviar.getStyle().set("width", "100%");
        enviar.getStyle().set("border-radius", "5px");

        enviar.addClickListener(e -> {
            try {
                usuarioService.registrarNuevoUsuario(
                    loginField.getValue(),
                    nombreField.getValue(),
                    passwordField.getValue()
                );
                Notification.show("Usuario registrado con éxito");
                registroLayout.removeAll();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        VerticalLayout formLayout = new VerticalLayout(nombreField, loginField, passwordField, enviar);
        formLayout.getStyle().set("padding", "20px");
        formLayout.getStyle().set("background-color", "white");
        formLayout.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        formLayout.getStyle().set("border-radius", "10px");

        registroLayout.add(formLayout);
    }
}
