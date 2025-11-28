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
    private SessionService sessionService;

    @Autowired
    private NavegacionComponent navegacion;

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ReaccionService reaccionService;

    private TextArea areaPublicacion;
    private Grid<Publicacion> tabla;
    private VerticalLayout comentariosLayout = new VerticalLayout();

    private TextArea areaComentario; // Para comentar en la publicación seleccionada
    private Button enviarComentario;

    private FileBuffer buffer = new FileBuffer();
    private Upload uploadComponent = new Upload(buffer);

    private static final String UPLOAD_DIR = "uploads" + File.separator;
    private Publicacion publicacionActual;

    public PublicacionesView(SessionService sessionService,
                             NavegacionComponent navegacion,
                             PublicacionService publicacionService) {
        this.sessionService = sessionService;
        this.navegacion = navegacion;
        this.publicacionService = publicacionService;

        UI.getCurrent().access(this::validarSesion);

        // Estilo azul
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setAlignItems(Alignment.CENTER);

        add(navegacion);

        // Área nueva publicación
        areaPublicacion = new TextArea("Nueva publicación");
        areaPublicacion.setWidth("80%");

        // Upload
        uploadComponent.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadComponent.setMaxFileSize(1024 * 1024 * 5);
        uploadComponent.setUploadButton(new Button("Seleccionar Foto"));
        uploadComponent.setWidth("200px");

        // Botón publicar
        Button publicarButton = new Button("Publicar", e -> publicar());
        publicarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        publicarButton.getStyle().set("border-radius", "10px");
        publicarButton.setWidth("150px");

        // Controles
        HorizontalLayout postControls = new HorizontalLayout(uploadComponent, publicarButton);
        postControls.setWidth("80%");
        postControls.setJustifyContentMode(JustifyContentMode.END);
        postControls.setAlignItems(Alignment.BASELINE);

        // Tabla de publicaciones
        tabla = new Grid<>(Publicacion.class, false);
        tabla.addColumn(p -> p.getAutor().getNombre()).setHeader("Autor");
        tabla.addColumn(Publicacion::getContenido).setHeader("Contenido");
        tabla.addColumn(p -> p.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
             .setHeader("Fecha");
        tabla.setWidth("80%");

        // Selección de publicación
        tabla.asSingleSelect().addValueChangeListener(e -> {
            publicacionActual = e.getValue();
            refrescarComentarios();
        });

        // Área para comentar en la publicación seleccionada
        areaComentario = new TextArea("Escribe un comentario...");
        areaComentario.setWidth("80%");
        enviarComentario = new Button("Comentar", e -> comentarPublicacion());
        HorizontalLayout comentarioControls = new HorizontalLayout(areaComentario, enviarComentario);
        comentarioControls.setWidth("80%");
        comentarioControls.setAlignItems(Alignment.BASELINE);

        // Layout principal
        add(areaPublicacion, postControls, tabla, comentarioControls, comentariosLayout);

        cargarFeed();
    }

    // ----------------------------------------
    // Publicar nueva publicación
    private void publicar() {
        String contenido = areaPublicacion.getValue();
        String login = sessionService.getLoginEnSesion();
        String rutaImagen = null;

        if ((contenido == null || contenido.isEmpty()) && buffer.getFileData() == null) {
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

            publicacionService.crearPublicacion(login, contenido, rutaImagen); // Se guarda ruta de imagen

            areaPublicacion.clear();
            buffer = new FileBuffer();
            uploadComponent.setReceiver(buffer);

            cargarFeed();
        } catch (Exception e) {
            Notification.show("Error publicando: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // Comentar en la publicación seleccionada
    private void comentarPublicacion() {
        String texto = areaComentario.getValue();
        if (texto == null || texto.isEmpty() || publicacionActual == null) {
            Notification.show("Debes escribir algo para comentar.");
            return;
        }

        try {
            comentarioService.crearComentario(
                    sessionService.getLoginEnSesion(),
                    publicacionActual.getId(),
                    texto,
                    null // comentario padre null
            );
            areaComentario.clear();
            refrescarComentarios();
        } catch (Exception e) {
            Notification.show("Error al comentar: " + e.getMessage());
        }
    }

    // ----------------------------------------
    // Cargar todas las publicaciones
    private void cargarFeed() {
        List<Publicacion> publicaciones = publicacionService.obtenerFeed();
        tabla.setItems(publicaciones);
    }

    // ----------------------------------------
    // Refrescar comentarios de la publicación actual
    private void refrescarComentarios() {
        comentariosLayout.removeAll();
        if (publicacionActual == null) return;

        List<Comentario> comentarios = comentarioService.obtenerComentarios(publicacionActual.getId());
        for (Comentario c : comentarios) {
            comentariosLayout.add(crearLayoutComentario(c));
        }
    }

    // ----------------------------------------
    // Crear layout de comentario + respuestas recursivas + reacciones
    private VerticalLayout crearLayoutComentario(Comentario c) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.getStyle().set("background-color", "#F0F8FF")
                          .set("padding", "10px")
                          .set("border-radius", "10px");

        Label autor = new Label(c.getAutor().getNombre() + " - " + c.getFecha());
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

        // Respuestas recursivas
        if (c.getRespuestas() != null) {
            for (Comentario r : c.getRespuestas()) {
                VerticalLayout respuestaLayout = crearLayoutComentario(r);
                respuestaLayout.getStyle().set("margin-left", "20px");
                layout.add(respuestaLayout);
            }
        }

        return layout;
    }

    // ----------------------------------------
    private int contarReacciones(Comentario c, Reaccion.TipoReaccion tipo) {
        if (c.getReacciones() == null) return 0;
        return (int) c.getReacciones().stream().filter(r -> r.getTipo() == tipo).count();
    }

    // ----------------------------------------
    private void mostrarTextAreaRespuesta(Comentario padre) {
        TextArea respuestaArea = new TextArea("Responder a " + padre.getAutor().getNombre());
        Button enviar = new Button("Enviar", e -> {
            String texto = respuestaArea.getValue();
            if (texto != null && !texto.isEmpty()) {
                try {
                    comentarioService.crearComentario(
                            sessionService.getLoginEnSesion(),
                            padre.getPublicacion().getId(),
                            texto,
                            padre.getId() // comentario padre
                    );
                    refrescarComentarios();
                } catch (Exception ex) {
                    Notification.show("Error al responder: " + ex.getMessage());
                }
            }
        });
        VerticalLayout contenedor = new VerticalLayout(respuestaArea, enviar);
        contenedor.setWidth("80%");
        comentariosLayout.add(contenedor);
    }

    // ----------------------------------------
    private void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
