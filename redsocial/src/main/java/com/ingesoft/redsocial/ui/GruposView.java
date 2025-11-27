package com.ingesoft.redsocial.ui;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
// Nueva importación para centrar la tabla
import com.vaadin.flow.component.orderedlayout.HorizontalLayout; 

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("grupos")
public class GruposView extends VerticalLayout {

    SessionService session;
    NavegacionComponent nav;
    GrupoService grupoService;

    TextField nombreGrupo;
    TextField descripcion;
    Button crearGrupo;

    Grid<Grupo> tabla;

    public GruposView(SessionService session, NavegacionComponent nav, GrupoService grupoService) {

        this.session = session;
        this.nav = nav;
        this.grupoService = grupoService;

        UI.getCurrent().access(this::validarSesion);

        // ********** MODIFICACIONES DE ESTILO **********
        
        // 1. Aplicar gama de colores azul de fondo
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        setAlignItems(Alignment.CENTER); // Centrar el contenido horizontalmente (campos, botón)
        
        add(nav);

        nombreGrupo = new TextField("Nombre del grupo");
        nombreGrupo.setWidth("300px"); 
        descripcion = new TextField("Descripción");
        descripcion.setWidth("300px");

        // Botón Crear Grupo
        crearGrupo = new Button("Crear Grupo", VaadinIcon.PLUS_CIRCLE.create(), e -> crear());
        crearGrupo.addThemeVariants(
            ButtonVariant.LUMO_PRIMARY,
            ButtonVariant.LUMO_LARGE
        );
        crearGrupo.getStyle().set("border-radius", "10px");
        crearGrupo.setWidth("300px");
        
        tabla = new Grid<>(Grupo.class);
        tabla.removeAllColumns();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo");
        // tabla.setWidth("70%"); // Eliminamos esta línea para usar el layout de centrado

        // 2. Centrar la Tabla dentro de un HorizontalLayout
        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%"); // Define el ancho del contenedor de la tabla (ajusta si es necesario)
        tablaContainer.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER); // Centra la tabla dentro de este contenedor
        tabla.setWidthFull(); // Hace que la tabla ocupe todo el ancho de su contenedor (tablaContainer)

        // 3. Añadir todos los componentes a la vista
        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        // ********** FIN DE MODIFICACIONES DE ESTILO **********
        
        cargar();
    }

    private void crear() {
        try {
            grupoService.crearGrupo(
                session.getLoginEnSesion(),
                nombreGrupo.getValue(),
                descripcion.getValue()
            );

            Notification.show("Grupo creado exitosamente");
            nombreGrupo.clear();
            descripcion.clear();
            cargar();

        } catch (Exception ex) {
            Notification.show("No fue posible crear el grupo: " + ex.getMessage());
        }
    }

    private void cargar() {
        tabla.setItems(grupoService.listarTodos());
    }

    private void validarSesion() {
        if (session.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
