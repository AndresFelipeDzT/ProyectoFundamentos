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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import com.ingesoft.redsocial.excepciones.ComentarioNotFoundException;
import com.ingesoft.redsocial.excepciones.PublicacionNotFoundException;
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
    private FileBuffer buffer = new FileBuffer();
    private Upload uploadComponent = new Upload(buffer);

    private static final String UPLOAD_DIR = "uploads" + File.separator;

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
        postControls.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END);
        postControls.setAlignItems(Alignment.BASELINE);

        tabla = new Grid<>(Publicacion.class, false);
        tabla.addColumn(Publicacion::getContenido).setHeader("Contenido").setAutoWidth(true);
        tabla.addColumn(p -> p.getAutor().getNombre()).setHeader("Autor").setAutoWidth(true);
        tabla.addColumn(p -> p.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
             .setHeader("Fecha").setAutoWidth(true);

        tabla.addComponentColumn(p -> {
            Button ver = new Button("Ver / Comentar", e -> abrirDialogPublicacion(p));
            ver.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return ver;
        }).setHeader("Acciones").setAutoWidth(true);

        tabla.setWidth("80%");
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

    private void abrirDialogPublicacion(Publicacion publicacion) {
        Publicacion pubCompleta = publicacionService.obtenerPorIdConComentarios(publicacion.getId());

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("80%");
        dialog.getElement().getStyle().set("background-color", "#E6F7FF");

        VerticalLayout contenido = new VerticalLayout();
        contenido.setWidthFull();
        contenido.setSpacing(true);

        Label autor = new Label(pubCompleta.getAutor().getNombre() + " - " +
                pubCompleta.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        Label texto = new Label(pubCompleta.getContenido());
        contenido.add(autor, texto);

        TextArea areaComentario = new TextArea("Escribe un comentario...");
        areaComentario.setWidthFull();
        VerticalLayout comentariosModal = new VerticalLayout();
        comentariosModal.setWidthFull();

        Button enviarComentario = new Button("Comentar", e -> {
            String textoComentario = areaComentario.getValue();
            if (textoComentario != null && !textoComentario.isEmpty()) {
                try {
                    comentarioService.crearComentario(sessionService.getLoginEnSesion(),
                            pubCompleta.getId(),
                            textoComentario,
                            null
                    );
                } catch (UsuarioNotFoundException | PublicacionNotFoundException ex) {
                    ex.printStackTrace();
                    Notification.show("Error al comentar: " + ex.getMessage());
                }
                areaComentario.clear();
                recargarComentarios(pubCompleta.getId(), comentariosModal);
            }
        });

        HorizontalLayout comentarioControls = new HorizontalLayout(areaComentario, enviarComentario);
        comentarioControls.setWidthFull();
        contenido.add(comentarioControls, comentariosModal);

        recargarComentarios(pubCompleta.getId(), comentariosModal);

        dialog.add(contenido);
        dialog.open();
    }

    private void recargarComentarios(Long publicacionId, VerticalLayout comentariosModal) {
        comentariosModal.removeAll();
        Publicacion actualizado = publicacionService.obtenerPorIdConComentarios(publicacionId);
        for (Comentario c : actualizado.getComentarios()) {
            comentariosModal.add(crearLayoutComentarioModal(c));
        }
    }

    private VerticalLayout crearLayoutComentarioModal(Comentario c) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("background-color", "#F0F8FF")
                          .set("padding", "10px")
                          .set("border-radius", "10px");

        Label autor = new Label(c.getAutor().getNombre() + " - " + c.getFecha());
        Label texto = new Label(c.getTexto());

        HorizontalLayout acciones = new HorizontalLayout();
        Label contadorLike = new Label(String.valueOf(contarReacciones(c, Reaccion.TipoReaccion.LIKE)));
        Label contadorDislike = new Label(String.valueOf(contarReacciones(c, Reaccion.TipoReaccion.DISLIKE)));

        Button like = new Button("👍", e -> {
            try {
                reaccionService.reaccionar(sessionService.getLoginEnSesion(), c.getId(), Reaccion.TipoReaccion.LIKE);
                recargarComentario(c, layout);
            } catch (UsuarioNotFoundException ex) { ex.printStackTrace(); } catch (ComentarioNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        Button dislike = new Button("👎", e -> {
            try {
                reaccionService.reaccionar(sessionService.getLoginEnSesion(), c.getId(), Reaccion.TipoReaccion.DISLIKE);
                recargarComentario(c, layout);
            } catch (UsuarioNotFoundException ex) { ex.printStackTrace(); } catch (ComentarioNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        acciones.add(like, contadorLike, dislike, contadorDislike);
        layout.add(autor, texto, acciones);

        if (c.getRespuestas() != null) {
            for (Comentario r : c.getRespuestas()) {
                VerticalLayout respuestaLayout = crearLayoutComentarioModal(r);
                respuestaLayout.getStyle().set("margin-left", "20px");
                layout.add(respuestaLayout);
            }
        }

        return layout;
    }

    private void recargarComentario(Comentario c, VerticalLayout layout) throws ComentarioNotFoundException {
        Comentario actualizado = comentarioService.obtenerPorIdConRespuestas(c.getId());
        layout.removeAll();
        Label autor = new Label(actualizado.getAutor().getNombre() + " - " + actualizado.getFecha());
        Label texto = new Label(actualizado.getTexto());

        HorizontalLayout acciones = new HorizontalLayout();
        Label contadorLike = new Label(String.valueOf(contarReacciones(actualizado, Reaccion.TipoReaccion.LIKE)));
        Label contadorDislike = new Label(String.valueOf(contarReacciones(actualizado, Reaccion.TipoReaccion.DISLIKE)));

        Button like = new Button("👍", e -> {
            try {
                reaccionService.reaccionar(sessionService.getLoginEnSesion(), actualizado.getId(), Reaccion.TipoReaccion.LIKE);
                recargarComentario(actualizado, layout);
            } catch (UsuarioNotFoundException ex) { ex.printStackTrace(); } catch (ComentarioNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        Button dislike = new Button("👎", e -> {
            try {
                reaccionService.reaccionar(sessionService.getLoginEnSesion(), actualizado.getId(), Reaccion.TipoReaccion.DISLIKE);
                recargarComentario(actualizado, layout);
            } catch (UsuarioNotFoundException ex) { ex.printStackTrace(); } catch (ComentarioNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        acciones.add(like, contadorLike, dislike, contadorDislike);
        layout.add(autor, texto, acciones);

        if (actualizado.getRespuestas() != null) {
            for (Comentario r : actualizado.getRespuestas()) {
                VerticalLayout respuestaLayout = crearLayoutComentarioModal(r);
                respuestaLayout.getStyle().set("margin-left", "20px");
                layout.add(respuestaLayout);
            }
        }
    }

    private int contarReacciones(Comentario c, Reaccion.TipoReaccion tipo) {
        return (int) c.getReacciones().stream().filter(r -> r.getTipo() == tipo).count();
    }

    private void validarSesion() {
        if (sessionService.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
