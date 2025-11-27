// LoginView.java
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
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

        mainLayout.setSizeFull();
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.getStyle().set("background-image", "url('images/red-network-bg.jpg')");
        mainLayout.getStyle().set("background-size", "cover");
        mainLayout.getStyle().set("background-position", "center");
        mainLayout.getStyle().set("background-repeat", "no-repeat");

        tituloComponent.getStyle().set("font-size", "36px");
        tituloComponent.getStyle().set("color", "#1a73e8");

        mostrarLogin();
        add(mainLayout);
    }

    private void mostrarLogin() {
        mainLayout.removeAll();
        mainLayout.add(tituloComponent);

        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.getStyle().set("width", "350px");
        mainLayout.add(loginForm);

        Label infoRegistro = new Label("¿Todavía no tienes una cuenta? Para registrar:");
        mainLayout.add(infoRegistro);

        Button botonIrRegistro = new Button("Registrar");
        botonIrRegistro.addClickListener(e -> mostrarRegistro());
        mainLayout.add(botonIrRegistro);

        loginForm.addLoginListener(event -> validaInicioSesion(event.getUsername(), event.getPassword()));
    }

    private void mostrarRegistro() {
        mainLayout.removeAll();
        mainLayout.add(tituloComponent);

        Label infoLogin = new Label("¿Ya tienes una cuenta? Para iniciar sesión:");
        mainLayout.add(infoLogin);

        Button botonIrLogin = new Button("Iniciar sesión");
        botonIrLogin.addClickListener(e -> mostrarLogin());
        mainLayout.add(botonIrLogin);

        TextField nombreField = new TextField("Nombre completo");
        TextField loginField = new TextField("Nombre de usuario");
        PasswordField passwordField = new PasswordField("Contraseña");
        Label passwordInfo = new Label("Mínimo 8 caracteres, incluye número y carácter especial");

        Button enviar = new Button("Registrar");
        enviar.addClickListener(e -> {
            try {
                usuarioService.registrarNuevoUsuario(
                        loginField.getValue(),
                        nombreField.getValue(),
                        passwordField.getValue()
                );
                Notification.show("Usuario registrado con éxito");
                mostrarLogin();
            } catch (Exception ex) {
                Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });

        VerticalLayout formLayout = new VerticalLayout(passwordInfo, nombreField, loginField, passwordField, enviar);
        mainLayout.add(formLayout);
    }

    private void validaInicioSesion(String username, String password) {
        try {
            usuarioService.iniciarSesion(username, password);
            session.setLoginEnSesion(username);
            UI.getCurrent().navigate(""); // página principal
        } catch (Exception e) {
            loginForm.setError(true);
            Notification.show("Error iniciando sesión: " + e.getMessage());
        }
    }
}
