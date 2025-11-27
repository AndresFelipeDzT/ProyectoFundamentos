package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

@Route("grupos")
public class GruposView extends VerticalLayout {

    @Autowired
    private GrupoService grupoService;

    private Grid<Grupo> tabla = new Grid<>(Grupo.class, false);

    public GruposView(GrupoService grupoService) {
        this.grupoService = grupoService;

        setPadding(true);
        setSpacing(true);

        H3 titulo = new H3("Grupos disponibles");

        // ---------- Tabla ----------
        tabla.addColumn(Grupo::getId).setHeader("ID");
        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Nombre");
        tabla.addColumn(Grupo::getDescripcion).setHeader("Descripción");
        tabla.addComponentColumn(this::crearBotonVer)
                .setHeader("Ver detalles");

        tabla.setItems(grupoService.obtenerTodos());

        add(titulo, tabla);
    }

    // ----------------------------------------------------------------------
    // BOTÓN DE VER DETALLES (Abre modal)
    // ----------------------------------------------------------------------
    private Button crearBotonVer(Grupo g) {
        Button btn = new Button("Ver",
                VaadinIcon.EYE.create(),
                e -> abrirDialogoGrupo(g));

        btn.getStyle().set("background-color", "#1976d2");
        btn.getStyle().set("color", "white");

        return btn;
    }

    // ----------------------------------------------------------------------
    // DIALOGO DEL GRUPO
    // ----------------------------------------------------------------------
    private void abrirDialogoGrupo(Grupo grupo) {

        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H3 titulo = new H3("Grupo: " + grupo.getNombreGrupo());
        Paragraph desc = new Paragraph(grupo.getDescripcion());

        // -------- LISTA DE PARTICIPANTES --------
        VerticalLayout lista = new VerticalLayout();
        lista.setPadding(false);

        lista.getStyle().set("border", "1px solid #CCC");
        lista.getStyle().set("max-height", "200px");
        lista.getStyle().set("overflow-y", "auto");

        // Se cargan dentro de la transacción → NO LANZA ERRORES
        grupoService.obtenerNombresParticipantes(grupo.getId())
                .forEach(nombre -> lista.add(new Paragraph(nombre)));

        // -------- AÑADIR PARTICIPANTE --------
        TextField campoLogin = new TextField("Usuario a añadir");
        Button btnAdd = new Button("Añadir", VaadinIcon.USERS.create(), e -> {
            try {
                grupoService.agregarParticipante(grupo.getId(), campoLogin.getValue());
                lista.add(new Paragraph(campoLogin.getValue()));
                campoLogin.clear();
            } catch (Exception ex) {
                lista.add(new Paragraph("⚠ " + ex.getMessage()));
            }
        });

        btnAdd.getStyle().set("background-color", "#43A047");
        btnAdd.getStyle().set("color", "white");

        // Cerrar
        Button cerrar = new Button("Cerrar", e -> dialog.close());

        dialog.add(
                titulo,
                desc,
                new H3("Participantes"),
                lista,
                new HorizontalLayout(campoLogin, btnAdd),
                cerrar
        );

        dialog.open();
    }
}
