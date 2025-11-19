package com.ingesoft.redsocial.ui.componentes;

import org.springframework.stereotype.Component;

import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;

@Component
@UIScope
public class NavegacionComponent extends VerticalLayout {

    // == Servicios de la aplicación

    SessionService sessionService;

    // == Componentes

    Button irAHome;
    Button irAAmigos;
    Button cerrarSesion;

    // == Constructor
    // - Crea el componente

    public NavegacionComponent(
        SessionService sessionService
    ) {

        this.sessionService = sessionService;

        HorizontalLayout barraTitulo = new HorizontalLayout();
        setWidthFull();

        HorizontalLayout titulo = new HorizontalLayout();
        titulo.setWidthFull();
        titulo.add(new H1("Red Social"));
        barraTitulo.add(titulo);
        
        HorizontalLayout botones = new HorizontalLayout();
        botones.setWidthFull();
        botones.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        irAHome = new Button("Home");
        irAHome.addClickListener(
            e -> navegarA("")
        );
        botones.add(irAHome);

        irAAmigos = new Button("Amigos");
        irAAmigos.addClickListener(
            e -> navegarA("amigos")   
        );
        botones.add(irAAmigos);

        cerrarSesion = new Button("Cerrar Sesión");
        cerrarSesion.addClickListener(
            e -> alSalir_CerrarSesion()
        );
        botones.add(cerrarSesion);

        barraTitulo.add(botones);

        add(barraTitulo);
        add(new Hr());

    } 

    // == Controladores / Eventos

	public void alSalir_CerrarSesion() {
		// muestra un mensaje
		Notification.show("Cerrando la sesión del usuario");
		
		// coloca en null el usuario en la sesión
		sessionService.setLoginEnSesion(null);
		// navega hacia la página de login
		UI.getCurrent().navigate("login");
	}

    // == Otros Métodos    

    public void navegarA(String pagina) {
        UI.getCurrent().navigate(pagina);
    }

}
