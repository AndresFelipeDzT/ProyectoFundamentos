package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.TituloComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login / Registro")
@AnonymousAllowed
public class LoginView extends Main {

    private final SessionService session;
    private final UsuarioService usuarioService;
    private final TituloComponent tituloComponent;

    private final VerticalLayout mainLayout = new VerticalLayout();
    private final LoginForm loginForm = new LoginForm();

    public LoginView(SessionService session, UsuarioService usuarioService, TituloComponent tituloComponent) {
        this.session = session;
        this.usuarioService = usuarioService;
        this.tituloComponent = tituloComponent;

        // Layout principal centrado
        mainLayout.setSizeFull();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.getStyle().set("background-image", "url('images/red-network-bg.jpg')");
        mainLayout.getStyle().set("background-size", "cover");
        mainLayout.getStyle().set("background-position", "center");
        mainLayout.getStyle().set("background-repeat", "no-repeat");

        // Título
        tituloComponent.getStyle().set("font-size", "36px");
        tituloComponent.getStyle().set("color", "#1a73e8");
        mainLayout.add(tituloComponent);

        // Mostrar login inicialmente
        mostrarLogin();

        add(mainLayout);
    }

    // =================== LOGIN ===================
    private void mostrarLogin() {
        mainLayout.removeAll();
        mainLayout.add(tituloComponent);

        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle().set("width", "350px");
        mainLayout.add(loginForm);

        Label infoRegistro = new Label("¿Todavía no tienes una cuenta? Para registrar:");
        infoRegistro.getStyle().set("font-size", "14px");
        infoRegistro.getStyle().set("color", "#555");
        mainLayout.add(infoRegistro);

        Button botonIrRegistro = new Button("Registrar");
        botonIrRegistro.getStyle().set("background-color", "#1a73e8");
        botonIrRegistro.getStyle().set("color", "white");
        botonIrRegistro.getStyle().set("margin-top", "10px");
        botonIrRegistro.getStyle().set("width", "150px");
        mainLayout.add(botonIrRegistro);

        botonIrRegistro.addClickListener(e -> mostrarRegistro());

        loginForm.addLoginListener(event -> validaInicioSesion(event.getUsername(), event.getPassword()));
    }

    private void validaInicioSesion(String username, String password) {
        if (authenticate(username, password)) {
            Notification.show("Inicio de sesión correcto para " + username);
            session.setLoginEnSesion(username);
            UI.getCurrent().navigate(""); // página principal
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

    // =================== REGISTRO ===================
    private void mostrarRegistro() {
        mainLayout.removeAll();
        mainLayout.add(tituloComponent);

        Label infoLogin = new Label("¿Ya tienes una cuenta? Para iniciar sesión:");
        infoLogin.getStyle().set("font-size", "14px");
        infoLogin.getStyle().set("color", "#555");
        mainLayout.add(infoLogin);

        Button botonIrLogin = new Button("Iniciar sesión");
        botonIrLogin.getStyle().set("background-color", "#1a73e8");
        botonIrLogin.getStyle().set("color", "white");
        botonIrLogin.getStyle().set("margin-bottom", "20px");
        botonIrLogin.getStyle().set("width", "150px");
        mainLayout.add(botonIrLogin);

        botonIrLogin.addClickListener(e -> mostrarLogin());

        // Formulario de registro
        TextField nombreField = new TextField("Nombre completo");
        TextField loginField = new TextField("Nombre de usuario");
        PasswordField passwordField = new PasswordField("Contraseña");

        // Texto discreto encima de la contraseña
        Span passwordHint = new Span("Mínimo 8 caracteres, incluye número y carácter especial");
        passwordHint.getStyle().set("font-size", "11px");
        passwordHint.getStyle().set("color", "gray");
        passwordHint.getStyle().set("margin-bottom", "2px");

        Button enviar = new Button("Registrar", e -> {
            try {
                usuarioService.registrarNuevoUsuario(
                    loginField.getValue(),
                    nombreField.getValue(),
                    passwordField.getValue()
                );
                Notification.show("Usuario registrado con éxito", 3000, Notification.Position.MIDDLE);
                mostrarLogin();
            } catch (Exception ex) {
                Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });

        enviar.getStyle().set("background-color", "#1a73e8");
        enviar.getStyle().set("color", "white");
        enviar.getStyle().set("width", "100%");
        enviar.getStyle().set("border-radius", "5px");

        VerticalLayout formLayout = new VerticalLayout(
            nombreField,
            loginField,
            passwordHint, // hint sobre contraseña
            passwordField,
            enviar
        );
        formLayout.setWidth("350px");
        formLayout.getStyle().set("padding", "20px");
        formLayout.getStyle().set("background-color", "white");
        formLayout.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        formLayout.getStyle().set("border-radius", "10px");

        mainLayout.add(formLayout);
    }
}
