package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.servicios.UsuarioService;
import com.ingesoft.redsocial.ui.componentes.TituloComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false) 
@PageTitle("Login")
@AnonymousAllowed 
public class LoginView extends Main {

   // == Servicios de la aplicación

    SessionService session;

    UsuarioService usuarioService;

    // == Componentes
    // - Elementos de la pantalla

    TituloComponent tituloComponent;

    private final LoginForm loginForm;

    // == Constructor
    // - Crea la pantalla

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

        // ********** INICIO DE MODIFICACIÓN **********

        // 1. Centrar el contenido horizontal y verticalmente
        getStyle().set("display", "flex");
        getStyle().set("flex-direction", "column"); // Organiza el contenido en columna
        getStyle().set("align-items", "center"); // Centra horizontalmente
        getStyle().set("justify-content", "center"); // Centra verticalmente

        // 2. Aplicar gama de colores azul
        // Fondo azul claro (puedes cambiar el color hexadecimal)
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        

        tituloComponent.setWidthFull(); // Opcional, pero ayuda a que el centrado de texto sea visible.
        tituloComponent.getStyle().set("text-align", "center");
        // ********** FIN DE MODIFICACIÓN **********


        add(tituloComponent);
        
        // agrega la pantalla de login
        loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        add(loginForm);

        // cuando se hace clic en iniciar sesión        
        loginForm.addLoginListener(event -> 
            validaInicioSesion(event.getUsername(), event.getPassword())
        );
        
    }
    
    // ... el resto del código (validaInicioSesion y authenticate) permanece igual ...

    public void validaInicioSesion(String username, String password) {
        // Hace la autenticación usando los datos de la pantalla
        // si la autenticación sale bien
        if (authenticate(username, password)) {
            // muestra un mensaje de inicio de sesión
            Notification.show("Inicia sesión para " + username);
            // asigna el usuario a la sesión
            session.setLoginEnSesion(username);
            // navega hacia la página principal
            UI.getCurrent().navigate("");
        // si la atenticación fall
        } else {
            // muestra un mensaje de error
            loginForm.setError(true);
            Notification.show("Error iniciando sesión", 3000, Notification.Position.MIDDLE);
        }
    }
    
    // autentica el usuario
    public boolean authenticate(String login, String password) {
        try {
            usuarioService.iniciarSesion(login, password);
            return true;        
        } catch (Exception e) {
            Notification.show("Error iniciando sesión:" + e.getMessage());
            return false;
        }
        // return (login.equals(password));
    }

}
