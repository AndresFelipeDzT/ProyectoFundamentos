package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private final GrupoService grupoService;
    private final SessionService session;

    public GruposView(GrupoService grupoService, SessionService session) {
        this.grupoService = grupoService;
        this.session = session;

        setWidthFull();

        mostrarGrupos();
    }

    private void mostrarGrupos() {
        removeAll();

        List<Grupo> grupos = grupoService.listarGrupos();

        for (Grupo g : grupos) {

            VerticalLayout tarjeta = new VerticalLayout();
            tarjeta.getStyle().set("border", "1px solid #ccc");
            tarjeta.getStyle().set("padding", "15px");
            tarjeta.getStyle().set("border-radius", "10px");
            tarjeta.getStyle().set("background-color", "#fff8f8"); // tu vinito suave
            tarjeta.setWidth("400px");

            tarjeta.add(new H3(g.getNombreGrupo()));
            tarjeta.add(new Paragraph(g.getDescripcion()));

            // -------- BOTÓN AÑADIR PARTICIPANTES ----------
            Button btnAñadir = new Button("Añadir");
            btnAñadir.getStyle().set("background-color", "#8c2f39"); // vino
            btnAñadir.getStyle().set("color", "white");
            btnAñadir.getStyle().set("border-radius", "6px");

            btnAñadir.addClickListener(e -> abrirDialogoAñadir(g));

            tarjeta.add(btnAñadir);

            add(tarjeta);
        }
    }

    // **************************************************************
    //          DIÁLOGO FLOTANTE: DESCRIPCIÓN + PARTICIPANTES
    // **************************************************************
    private void abrirDialogoAñadir(Grupo grupo) {

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("450px");

        // Título + botón cerrar
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.add(new H3(grupo.getNombreGrupo()));

        Button cerrar = new Button("X", e -> dialog.close());
        cerrar.getStyle().set("background-color", "transparent");
        cerrar.getStyle().set("color", "black");

        header.add(cerrar);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Descripción
        Paragraph descripcion = new Paragraph(grupo.getDescripcion());
        descripcion.getStyle().set("font-size", "14px");

        // ******** LISTA SCROLLABLE DE PARTICIPANTES ********
        MultiSelectListBox<String> lista = new MultiSelectListBox<>();
        lista.setItems(grupo.getLoginsParticipantes());

        lista.getStyle().set("border", "1px solid #ccc");
        lista.getStyle().set("padding", "10px");
        lista.setHeight("200px"); // scroll

        // Botón confirmar añadir
        Button agregar = new Button("Añadir Participante");
        agregar.getStyle().set("background-color", "#8c2f39");
        agregar.getStyle().set("color", "white");

        agregar.addClickListener(e -> {

            String login = session.getLoginEnSesion();

            try {
                grupoService.unirUsuarioAGrupo(login, grupo.getId());
                Notification.show("Te uniste al grupo " + grupo.getNombreGrupo());

                dialog.close();
                mostrarGrupos(); // refresca
            } catch (Exception ex) {
                Notification.show(ex.getMessage());
            }
        });

        VerticalLayout contenido = new VerticalLayout(header, descripcion, lista, agregar);
        contenido.setPadding(true);

        dialog.add(contenido);
        dialog.open();
    }
}
