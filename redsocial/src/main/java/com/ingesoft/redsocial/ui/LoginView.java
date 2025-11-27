package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.TituloComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

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

        // Layout principal centrado con fondo
        mainLayout.setSizeFull();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.getStyle().set("background-color", "#E6F7FF"); // azul claro opcional

        // Título
        tituloComponent.getStyle().set("font-size", "36px");
        tituloComponent.getStyle().set("color", "#1a73e8");
        mainLayout.add(tituloComponent);

        // Mostrar login al inicio
        mostrarLogin();
        add(mainLayout);
    }

    // =================== LOGIN ===================
    private void mostrarLogin() {
        mainLayout.removeAll();
        mainLayout.add(tituloComponent);

        Label infoRegistro = new Label("¿Todavía no tienes una cuenta? Para registrar:");
        infoRegistro.getStyle().set("font-size", "14px");
        infoRegistro.getStyle().set("color", "#555");
        mainLayout.add(infoRegistro);

        Button botonIrRegistro = new Button("Registrar");
        botonIrRegistro.getStyle().set("background-color", "#1a73e8");
        botonIrRegistro.getStyle().set("color", "white");
        botonIrRegistro.getStyle().set("margin-top", "10px");
        mainLayout.add(botonIrRegistro);

        botonIrRegistro.addClickListener(e -> mostrarRegistro());

        loginForm.setForgotPasswordButtonVisible(false);
        mainLayout.add(loginForm);

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
        mainLayout.add(botonIrLogin);

        botonIrLogin.addClickListener(e -> mostrarLogin());

        TextField nombreField = new TextField("Nombre completo");
        TextField loginField = new TextField("Nombre de usuario");
        PasswordField passwordField = new PasswordField("Contraseña");

        Label passwordInfo = new Label("Mínimo 8 caracteres, incluye número y carácter especial");
        passwordInfo.getStyle().set("font-size", "12px");
        passwordInfo.getStyle().set("color", "gray");

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
                Notification.show("Usuario registrado con éxito", 3000, Notification.Position.MIDDLE);
                mostrarLogin();
            } catch (Exception ex) {
                Notification.show(ex.getMessage(), 4000, Notif
