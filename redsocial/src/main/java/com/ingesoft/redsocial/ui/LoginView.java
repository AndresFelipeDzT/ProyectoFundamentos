package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.TituloComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
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

    // == Servicios de la aplicación
    private final SessionService session;
    private final UsuarioService usuarioService;
    private final TituloComponent tituloComponent;

    // == Componentes de la pantalla
    private final LoginForm loginForm;
    private final VerticalLayout registroLayout = new VerticalLayout();

    // == Constructor
    public LoginView(
        SessionService session,
        UsuarioService usuarioService,
        TituloComponent tituloComponent
    ) {
        this.session = session;
        this.usuarioService = usuarioService;
        this.tituloComponent = tituloComponent;

        setSizeFull();
        getStyle().set("flex-grow", "1");

        // Título de la pantalla
        add(tituloComponent);

        // LoginForm
        loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        add(loginForm);

        // Botón para mostrar formulario de registro
        Button botonRegistrar = new Button("Registrar");
        add(botonRegistrar);

        botonRegistrar.addClickListener(event -> mostrarFormularioRegistro());

        // Layout para el registro (inicialmente vacío)
        add(registroLayout);

        // Listener del login
        loginForm.addLoginListener(event -> validaInicioSesion(event.getUsername(), event.getPassword()));
    }

    // == Método para login
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

    // == Método para mostrar el formulario de registro
    private void mostrarFormularioRegistro() {
        registroLayout.removeAll(); // limpiar si ya hay algo

        TextField nombreField = new TextField("Nombre completo");
        TextField loginField = new TextField("Nombre de usuario");
        PasswordField passwordField = new PasswordField("Contraseña");

        Button enviar = new Button("Registrar");
        enviar.addClickListener(e -> {
            try {
                usuarioService.registrarNuevoUsuario(
                    loginField.getValue(),
                    nombreField.getValue(),
                    passwordField.getValue()
                );
                Notification.show("Usuario registrado con éxito");
                registroLayout.removeAll(); // limpiar formulario
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        registroLayout.add(nombreField, loginField, passwordField, enviar);
    }
}
