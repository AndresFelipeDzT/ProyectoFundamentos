package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private final GrupoService grupoService;
    private final Grid<Grupo> gridGrupos = new Grid<>(Grupo.class, false);

    public GruposView(GrupoService grupoService) {
        this.grupoService = grupoService;

        setPadding(true);
        setSpacing(true);

        // -----------------------------
        // CONFIGURACIÓN DE LA TABLA
        // -----------------------------
        gridGrupos.addColumn(Grupo::getId).setHeader("ID");
        gridGrupos.addColumn(Grupo::getNombreGrupo).setHeader("Nombre");
        gridGrupos.addColumn(Grupo::getDescripcion).setHeader("Descripción");

        // Columna de participantes (solo número, no lista completa)
        gridGrupos.addColumn(g -> g.getLoginsParticipantes().size())
                .setHeader("Participantes");

        // -----------------------------
        // COLUMNA DE ACCIONES → AÑADIR
        // -----------------------------
        gridGrupos.addComponentColumn(grupo -> {
            Button btnAdd = new Button("Añadir");
            btnAdd.getStyle().set("background-color", "#5A0E2E"); // vinito
            btnAdd.getStyle().set("color", "white");

            btnAdd.addClickListener(e -> añadirUsuarioAGrupo(grupo));
            return btnAdd;
        }).setHeader("Acciones");

        actualizarTabla();

        add(gridGrupos);
    }

    // Refresca la tabla
    private void actualizarTabla() {
        gridGrupos.setItems(grupoService.obtenerTodos());
    }

    // Acción para añadir usuario (usa tu método existente)
    private void añadirUsuarioAGrupo(Grupo grupo) {
        try {
            String loginActual = "ana"; // <<< O el usuario logueado, usa el tuyo si lo manejas
            grupoService.unirUsuarioAGrupo(loginActual, grupo.getId());
            Notification.show("Usuario añadido al grupo");
            actualizarTabla();

        } catch (Exception ex) {
            Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }
}
