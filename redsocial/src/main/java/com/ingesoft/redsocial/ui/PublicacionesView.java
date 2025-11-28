package com.ingesoft.redsocial.ui;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Comentario;
import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.modelo.Reaccion;
import com.ingesoft.redsocial.servicios.ComentarioService;
import com.ingesoft.redsocial.servicios.PublicacionService;
import com.ingesoft.redsocial.servicios.ReaccionService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;

@Route("publicaciones")
@PageTitle("Publicaciones")
public class PublicacionesView extends VerticalLayout {

    @Autowired
    SessionService sessionService;

    @Autowired
    NavegacionComponent navegacion;

    @Autowired
    PublicacionService publicacionService;

    @Autowired
    ComentarioService comentarioService;

    @Autowired
    ReaccionService reaccionService;

    TextArea areaPublicacion;
    Grid<Publicacion> tabla;
    VerticalLayout comentariosLayout = new VerticalLayout();
    FileBuffer buffer = new FileBuffer();
    Upload uploadComponent = new Upload(buffer);
    private static final String UPLOAD_DIR = "uploads" + File.separator;
    private Publicacion publicacionActual;

    public PublicacionesView(SessionService sessionService,
                             NavegacionComponent navegacion,
                             PublicacionService publicacionService) {
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
        uploadComponent.setUploadButton(new Button("Seleccionar Foto"));
        uploadComponent.setWidth("200px");

        Button publicarButton = new Button("Publicar", e -> publicar());
        publicarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        publicarButton.getStyle().set("border-radius", "10px");
        publicarButton.setWidth("150px");

        HorizontalLayout postControls = new HorizontalLayout(uploadComponent, publicarButton);
        postControls.setWidth("80%");
        postControls.setJustifyContentMode(JustifyContentMode.END);
        postControls.setAlignItems(Alignment.BASELINE);

        tabla = new Grid<>(Publicacion.class, false);
        tabla.addColumn(p -> p.getAutor().getNombre()).setHeader("Autor");
        tabla.addColumn(Publicacion::getContenido).setHeader("Contenido");
        tabla.addColumn(p -> p.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
             .setHeader("Fecha");
        tabla.setWidth("80%");
        tabla.asSingleSelect().addValueChangeListener(e -> {
            publicacionActual = e.getValue();
            refrescarComentarios();
        });

        add(areaPublicacion, postControls, tabla, comentariosLayout);

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
            }

            // Ahora sí pasamos rutaImagen
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

    private void refrescarComentarios() {
        comentariosLayout.removeAll();
        if (publicacionActual == null) return;

        List<Comentario> comentarios = comentarioService.obtenerComentarios(publicacionActual.getId());
        for (Comentario c : comentarios) {
            comentariosLayout.add(crearLayoutComentario(c));
        }
    }

    private VerticalLayout crearLayoutComentario(Comentario c) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.getStyle().set("background-color", "#F0F8FF")
                          .set("padding", "10px")
                          .set("border-radius", "10px");

        Label autor = new Label(c.getAutor().getNombre() + " - " +
                c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        Label texto = new Label(c.getTexto());

        HorizontalLayout acciones = new HorizontalLayout();
        Button like = new Button("👍 " + contarReacciones(c, Reaccion.TipoReaccion.LIKE), e -> {
            try {
                reaccionService.reaccionar(sessionService.getLoginEnSesion(), c.getId(), Reaccion.TipoReaccion.LIKE);
            } catch (UsuarioNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            refrescarComentarios();
        });
        Button dislike = new Button("👎 " + contarReacciones(c, Reaccion.TipoReaccion.DISLIKE), e -> {
            try {
                reaccionService.reaccionar(sessionService.getLoginEnSesion(), c.getId(), Reaccion.TipoReaccion.DISLIKE);
            } catch (UsuarioNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            refrescarComentarios();
        });
        Button responder = new Button("Responder", e -> mostrarTextAreaRespuesta(c));

        acciones.add(like, dislike, responder);
        layout.add(autor, texto, acciones);

        if (c.getRespuestas() != null) {
            for (Comentario r : c.getRespuestas()) {
                VerticalLayout respuestaLayout = crearLayoutComentario(r);
                respuestaLayout.getStyle().set("margin-left", "20px");
                layout.add(respuestaLayout);
            }
        }

        return layout;
    }

    private int contarReacciones(Comentario c, Reaccion.TipoReaccion tipo) {
        return (int) c.getReacciones().stream().filter(r -> r.getTipo() == tipo).count();
    }

    private void mostrarTextAreaRespuesta(Comentario padre) {
        TextArea respuestaArea = new TextArea("Responder a " + padre.getAutor().getNombre());
        Button enviar = new Button("Enviar", e -> {
            String texto = respuestaArea.getValue();
            if (!texto.isEmpty()) {
                try {
                    comentarioService.crearComentario(
                            sessionService.getLoginEnSesion(),
                            padre.getPublicacion().getId(),
                            texto,
                            padre.getId()  // Aquí pasamos idPadre
                    );
                } catch (UsuarioNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                refrescarComentarios();
            }
        });
        VerticalLayout contenedor = new VerticalLayout(respuestaArea, enviar);
        contenedor.setWidth("80%");
        comentariosLayout.add(contenedor);
    }

    private void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
