package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout {
	
	// == Servicios de la aplicación

    SessionService sessionService;

    // == Componentes
    // - Elementos de la pantalla

	NavegacionComponent navegacion;

    // == Constructor
    // - Crea la pantalla

	public HomeView(
		SessionService sessionService,
		NavegacionComponent navegacion
	) {

		this.sessionService = sessionService;
		this.navegacion = navegacion;

		// al momento de cargar la pantalla
		UI.getCurrent().access(() -> {
			alInicio_RevisarSesion();
		});

		// == pantalla a mostrar

		add(navegacion);

		// == agregar otros elementos de la pantalla




	}


	// == Controladores 
    // - obtiene los datos de la solicitud de la pantalla
    // - invoca a los servicios / la lógica de negocio
    // - actualiza la pantalla

	public void alInicio_RevisarSesion() {
		// si no hay nadie en la sesión
		if (sessionService.getLoginEnSesion() == null) {
			// debe ir a la página de login
			UI.getCurrent().navigate("login");
		}
	}

	public void alSalir_CerrarSesion() {
		// muestra un mensaje
		Notification.show("Cerrando la sesión del usuario");
		
		// coloca en null el usuario en la sesión
		sessionService.setLoginEnSesion(null);
		// navega hacia la página de login
		UI.getCurrent().navigate("login");
	}


    // == Otros Métodos
    // - para invocar la lógica de negocio más fácil

}