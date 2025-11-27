package com.ingesoft.redsocial.ui;

// Importaciones necesarias para los nuevos componentes
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

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

		// ********** INICIO DE MODIFICACIONES CORREGIDAS **********

		// Configurar la vista completa
        setSizeFull(); 
        
        // 1. Aplicar gama de colores azul de fondo
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        
        // 2. Centrar el contenido horizontalmente
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        
        // El resto del código de la vista
		add(navegacion);


        // 3. Crear los botones con iconos, tamaño grande y esquinas redondeadas con CSS
        
        // Estilo de botón común para simplificar el código
        var commonVariants = new com.vaadin.flow.component.button.ButtonVariant[]{
            com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY, 
            com.vaadin.flow.component.button.ButtonVariant.LUMO_LARGE
        };
        String borderRadius = "15px"; // Radio para esquinas redondeadas


        // Botón Publicaciones/Muro
        Button publicacionesButton = new Button("Publicaciones", VaadinIcon.NEWSPAPER.create(), 
            e -> UI.getCurrent().navigate(PublicacionesView.class)
        );
        publicacionesButton.setWidth("300px"); 
        publicacionesButton.addThemeVariants(commonVariants);
        // FIX: Aplicar borde redondeado con CSS
        publicacionesButton.getStyle().set("border-radius", borderRadius); 

        // Botón Amigos
        Button amigosButton = new Button("Amigos", VaadinIcon.GROUP.create(), 
            e -> UI.getCurrent().navigate(AmigosView.class)
        );
        amigosButton.setWidth("300px");
        amigosButton.addThemeVariants(commonVariants);
        // FIX: Aplicar borde redondeado con CSS
        amigosButton.getStyle().set("border-radius", borderRadius); 

        // Botón Grupos
        Button gruposButton = new Button("Grupos", VaadinIcon.USERS.create(), 
            e -> UI.getCurrent().navigate(GruposView.class)
        );
        gruposButton.setWidth("300px");
        gruposButton.addThemeVariants(commonVariants);
        // FIX: Aplicar borde redondeado con CSS
        gruposButton.getStyle().set("border-radius", borderRadius); 
        
        // Botón Perfil
        Button perfilButton = new Button("Mi Perfil", VaadinIcon.USER.create(), 
            e -> UI.getCurrent().navigate(PerfilView.class)
        );
        perfilButton.setWidth("300px");
        perfilButton.addThemeVariants(commonVariants);
        // FIX: Aplicar borde redondeado con CSS
        perfilButton.getStyle().set("border-radius", borderRadius); 

        // Contenedor para los botones
        VerticalLayout botonesContainer = new VerticalLayout(
            publicacionesButton,
            amigosButton,
            gruposButton,
            perfilButton
        );
        botonesContainer.setAlignItems(Alignment.CENTER);
        botonesContainer.setSpacing(true); 
        
        // Añadir los botones a la vista
        add(botonesContainer);

		// ********** FIN DE MODIFICACIONES CORREGIDAS **********

		// al momento de cargar la pantalla
		UI.getCurrent().access(() -> {
			alInicio_RevisarSesion();
		});

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

}
