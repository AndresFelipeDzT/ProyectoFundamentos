package com.ingesoft.redsocial.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import java.util.List;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private final SessionService session;
    private final GrupoService grupoService;

    private final TextField nombreGrupo;
    private final TextField descripcion;
    private final Button crearGrupo;
    private final Grid<Grupo> tabla;

    public GruposView(SessionService session, GrupoService grupoService) {
        this.session = session;
        this.grupoService = grupoService;

        setSizeFull();
        getStyle().set("background-color", "#E6F7FF"); // Azul claro
        setAlignItems(Alignment.CENTER);

        nombreGrupo = new TextField("Nombre del grupo");
        nombreGrupo.setWidth("300px");

        descripcion = new TextField("Descripción");
        descripcion.setWidth("300px");

        crearGrupo = new Button("Crear Grupo", e -> crear());
        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearGrupo.getStyle().set("border-radius", "10px");
        crearGrupo.setWidth("300px");

        tabla = new Grid<>(Grupo.class);
        tabla.removeAllColumns();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Nombre");
        tabla.addColumn(Grupo::getDescripcion).setHeader("Descripción");
        tabla.addColumn(g -> g.getParticipantes().size()).setHeader("Miembros");
        tabla.addComponentColumn(g -> crearBotonUnirse(g)).setHeader("Acción");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        cargar();
    }

    private Button crearBotonUnirse(Grupo grupo) {
        Button boton = new Button("Unirse");
        boton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        boton.addClickListener(e -> {
            try {
                grupoService.unirseAGrupo(session.getLoginEnSesion(), grupo.getId());
                Notification.show("Te uniste al grupo: " + grupo.getNombreGrupo());
                cargar();
            } catch (Exception ex) {
                Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        return boton;
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
            Notification.show("No fue posible crear el grupo: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void cargar() {
        List<Grupo> grupos = grupoService.listarTodos();
        tabla.setItems(grupos);
    }
}
