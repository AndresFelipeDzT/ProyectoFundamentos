package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;
import com.ingesoft.redsocial.servicios.UsuarioService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("amigos")
@PageTitle("Amigos")
public class AmigosView extends VerticalLayout {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    private Usuario usuarioActual;

    public AmigosView(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;

        setSizeFull();
        setSpacing(true);
        setPadding(true);

        add(new H1("Amigos"));
    }

    /**
     * VALIDACIÓN DE SESIÓN — se ejecuta cuando la vista ya existe en la UI.
     * ESTA es la forma correcta. Nada de access(), nada en constructor.
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        String login = (String) UI.getCurrent().getSession().getAttribute("login");

        if (login == null) {
            Notification.show("Debes iniciar sesión para acceder a esta vista");
            UI.getCurrent().navigate("login");
            return;
        }

        try {
            usuarioActual = usuarioService.obtenerPorLogin(login);
        } catch (Exception e) {
            Notification.show("Error cargando usuario: " + e.getMessage());
            UI.getCurrent().navigate("login");
            return;
        }

        // Después de validar sesión, puedes cargar todo lo demás
        cargarContenido();
    }

    /**
     * Aquí va todo lo que la vista muestra después de validar sesión
     */
    private void cargarContenido() {

        removeAll(); // Limpia el layout para volver a construirlo

        add(new H1("Amigos de " + usuarioActual.getNombre()));

        Button btnBuscar = new Button("Buscar personas", e ->
                UI.getCurrent().navigate("buscar-persona")
        );

        add(btnBuscar);

        // TODO: añadir más cosas de la vista si lo necesitas
    }
}
