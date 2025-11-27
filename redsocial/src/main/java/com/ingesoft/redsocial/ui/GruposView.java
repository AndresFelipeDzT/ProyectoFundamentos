package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.ingesoft.redsocial.excepciones.GrupoExistenteException;
import com.ingesoft.redsocial.excepciones.GrupoNotFoundException;
import com.ingesoft.redsocial.excepciones.UsuarioAlreadyInGroupException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

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
        setPadding(true);
        setSpacing(true);

        // Campos de creación de grupo
        nombreGrupo = new TextField("Nombre del grupo");
        descripcion = new TextField("Descripción");
        crearGrupo = new Button("Crear Grupo", e -> crear());
        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Tabla de grupos
        tabla = new Grid<>(Grupo.class, false);
        tabla.setWidthFull();
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo").setAutoWidth(true);
        tabla.addColumn(g -> g.getParticipantes().size()).setHeader("Miembros").setAutoWidth(true);
        tabla.addComponentColumn(g -> {
            Button unirse = new Button("Unirse", e -> {
                try {
                    grupoService.unirseAGrupo(session.getLoginEnSesion(), g.getId());
                    Notification.show("Te uniste al grupo '" + g.getNombreGrupo() + "'", 3000, Notification.Position.TOP_CENTER);
                    tabla.setItems(grupoService.listarTodos());
                } catch (UsuarioAlreadyInGroupException ex) {
                    Notification.show("Ya eres miembro de este grupo", 3000, Notification.Position.TOP_CENTER);
                } catch (GrupoNotFoundException ex) {
                    Notification.show("Grupo no encontrado, contacte al administrador", 3000, Notification.Position.TOP_CENTER);
                } catch (Exception ex) {
                    Notification.show("Error inesperado, contacte al administrador", 3000, Notification.Position.TOP_CENTER);
                }
            });
            unirse.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return unirse;
        }).setHeader("Acción");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidthFull();

        // Añadir todos los componentes
        add(nombreGrupo, descripcion, crearGrupo, tablaContainer);

        // Cargar la lista de grupos
        cargar();
    }

    private void crear() {
        try {
            grupoService.crearGrupo(session.getLoginEnSesion(), nombreGrupo.getValue(), descripcion.getValue());
            Notification.show("Grupo creado exitosamente", 3000, Notification.Position.TOP_CENTER);
            nombreGrupo.clear();
            descripcion.clear();
            tabla.setItems(grupoService.listarTodos());
        } catch (GrupoExistenteException ex) {
            Notification.show("Ya existe un grupo con ese nombre", 3000, Notification.Position.TOP_CENTER);
        } catch (Exception ex) {
            Notification.show("Error creando grupo, contacte al administrador", 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void cargar() {
        try {
            tabla.setItems(grupoService.listarTodos());
        } catch (Exception ex) {
            Notification.show("Error cargando grupos, contacte al administrador", 3000, Notification.Position.TOP_CENTER);
        }
    }
}
