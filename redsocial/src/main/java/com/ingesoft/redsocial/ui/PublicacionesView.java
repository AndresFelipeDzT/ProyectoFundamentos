package com.ingesoft.redsocial.ui;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

// Importaciones para subida de archivos y estilo
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout; 
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
// FIX: Nueva importación necesaria para JustifyContentMode
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode; 

import com.ingesoft.redsocial.modelo.Publicacion;
import com.ingesoft.redsocial.servicios.PublicacionService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

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
    
    // Directorio donde se guardarán temporalmente las imágenes (AJUSTAR SEGÚN TU PROYECTO)
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

        // ********** ESTILO AZUL **********
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF"); // Fondo Azul claro
        setAlignItems(Alignment.CENTER); // Centrar contenido horizontalmente
        // ********** ESTILO AZUL **********
        
        add(navegacion);

        areaPublicacion = new TextArea("Nueva publicación");
        areaPublicacion.setWidth("80%"); // Ancho limitado para centrar
        
        // 1. Configuración del componente de subida de archivos (Upload)
        uploadComponent.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadComponent.setMaxFileSize(1024 * 1024 * 5); // 5MB máximo
        uploadComponent.setUploadButton(new com.vaadin.flow.component.button.Button("Seleccionar Foto"));
        uploadComponent.setWidth("200px");

        // 2. Botón Publicar con estilo azul y esquinas redondeadas
        var publicarButton = new com.vaadin.flow.component.button.Button("Publicar", e -> publicar());
        publicarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        publicarButton.getStyle().set("border-radius", "10px");
        publicarButton.setWidth("150px");


        // 3. Agrupar los controles de publicación
        HorizontalLayout postControls = new HorizontalLayout(uploadComponent, publicarButton);
        postControls.setWidth("80%");
        // 🛑 FIX: Usar JustifyContentMode.END
        postControls.setJustifyContentMode(JustifyContentMode.END); 
        postControls.setAlignItems(Alignment.BASELINE); 


        tabla = new Grid<>(Publicacion.class);
        tabla.removeAllColumns();
        tabla.addColumn(p -> p.getAutor().getNombre()).setHeader("Autor");
        tabla.addColumn(Publicacion::getContenido).setHeader("Contenido");
        tabla.addColumn(Publicacion::getFechaCreacion).setHeader("Fecha");
        tabla.setWidth("80%"); 

        // 4. Añadir todos los componentes a la vista
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
            // Lógica de manejo de archivos
            if (buffer.getFileData() != null) {
                
                // 🛑 PASO 1: CREAR LA CARPETA DE SUBIDA SI NO EXISTE
                File uploadDirectory = new File(UPLOAD_DIR);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs();
                }

                // Generar nombre de archivo único
                String fileName = login + "_" + System.currentTimeMillis() + "_" + buffer.getFileName();
                File file = new File(UPLOAD_DIR + fileName);

                // 🛑 PASO 2: GUARDAR EL ARCHIVO TEMPORALMENTE
                try (InputStream inputStream = buffer.getInputStream()) {
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    // Aquí se guarda la ruta del sistema de archivos
                    rutaImagen = UPLOAD_DIR + fileName; 
                }
                
                Notification.show("Foto lista para publicación: " + fileName);
            }
            
            // 🛑 PASO 3: CREAR LA PUBLICACIÓN (AJUSTAR EL SERVICIO)
            // Recuerda modificar PublicacionService.crearPublicacion para que reciba y guarde 'rutaImagen'.
            // publicacionService.crearPublicacion(login, contenido, rutaImagen);
            publicacionService.crearPublicacion(login, contenido); 
            
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
