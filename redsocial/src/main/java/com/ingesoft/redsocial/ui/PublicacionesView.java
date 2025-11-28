package com.ingesoft.redsocial.ui;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.servicios.PublicacionService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;

@Route("publicaciones")
@PageTitle("Publicaciones")
public class PublicacionesView extends VerticalLayout {

    SessionService sessionService;
    NavegacionComponent navegacion;
    PublicacionService publicacionService;

    TextArea areaPublicacion;
    Grid<Publicacion> tabla;

    // Componentes de la foto
    FileBuffer buffer = new FileBuffer();
    Upload uploadComponent = new Upload(buffer);

    private static final String UPLOAD_DIR = "uploads" + File.separator;

    public PublicacionesView(
        SessionService sessionService,
        NavegacionComponent navegacion,
        PublicacionService publicacionService
    ) {
        this.sessionService = sessionService;
        this.navegacion = navegacion;
        this.publicacionService = publicacionService;

        UI.getCurrent().access(this::validarSesion);

        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setAlignItems(Alignment.CENTER);

        add(navegacion);

        areaPublicacion = new TextArea("Nueva publicación");
        areaPublicacion.setWidth("80%");

        uploadComponent.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadComponent.setMaxFileSize(1024 * 1024 * 5);
        uploadComponent.setUploadButton(new com.vaadin.flow.component.button.Button("Seleccionar Foto"));
        uploadComponent.setWidth("200px");

        var publicarButton = new com.vaadin.flow.component.button.Button("Publicar", e -> publicar());
        publicarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        publicarButton.getStyle().set("border-radius", "10px");
        publicarButton.setWidth("150px");

        HorizontalLayout postControls = new HorizontalLayout(uploadComponent, publicarButton);
        postControls.setWidth("80%");
        postControls.setJustifyContentMode(JustifyContentMode.END);
        postControls.setAlignItems(Alignment.BASELINE);

        tabla = new Grid<>(Publicacion.class, false);
        tabla.setWidth("80%");
        tabla.addComponentColumn(p -> {
            VerticalLayout layout = new VerticalLayout();
            layout.add(new com.vaadin.flow.component.html.Label(p.getContenido()));
            if (p.getRutaArchivo() != null) {
                Image img = new Image(p.getRutaArchivo(), "Foto adjunta");
                img.setHeight("150px");
                layout.add(img);
            }
            return layout;
        }).setHeader("Contenido");

        tabla.addColumn(p -> p.getAutor().getNombre()).setHeader("Autor");
        tabla.addColumn(Publicacion::getFechaCreacion).setHeader("Fecha");

        add(areaPublicacion, postControls, tabla);

        cargarFeed();
    }

    private void publicar() {
        String contenido = areaPublicacion.getValue();
        String login = sessionService.getLoginEnSesion();
        String rutaImagen = null;

        if (contenido.isEmpty() && buffer.getFileData() == null) {
            Notification.show("Debes escribir algo o seleccionar una foto.");
            return;
        }

        try {
            if (buffer.getFileData() != null) {
                File uploadDirectory = new File(UPLOAD_DIR);
                if (!uploadDirectory.exists()) uploadDirectory.mkdirs();

                String fileName = login + "_" + System.currentTimeMillis() + "_" + buffer.getFileName();
                File file = new File(UPLOAD_DIR + fileName);

                try (InputStream inputStream = buffer.getInputStream()) {
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    rutaImagen = UPLOAD_DIR + fileName;
                }

                Notification.show("Foto lista para publicación: " + fileName);
            }

            publicacionService.crearPublicacion(login, contenido, rutaImagen);

            areaPublicacion.clear();
            buffer = new FileBuffer();
            uploadComponent.setReceiver(buffer);

            cargarFeed();

        } catch (Exception e) {
            Notification.show("Error publicando: " + e.getMessage());
        }
    }

    private void cargarFeed() {
        List<Publicacion> publicaciones = publicacionService.obtenerFeed();
        tabla.setItems(publicaciones);
    }

    private void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
