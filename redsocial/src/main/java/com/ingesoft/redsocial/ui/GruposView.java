package com.ingesoft.redsocial.ui;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;
import com.ingesoft.redsocial.servicios.GrupoService;
import com.ingesoft.redsocial.ui.componentes.NavegacionComponent;
import com.ingesoft.redsocial.ui.servicio.SessionService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;


import java.util.List;

@Route("grupos")
public class GruposView extends VerticalLayout {

    private TextField nombreGrupo;
    private TextField descripcion;
    private Button crearGrupo;

    private Grid<Grupo> tabla;

    private final SessionService session;
    private final NavegacionComponent nav;
    private final GrupoService grupoService;
    private final UsuarioRepository usuarioRepository;


    public GruposView(SessionService session,
                    NavegacionComponent nav,
                    GrupoService grupoService,
                    UsuarioRepository usuarioRepository) {

        this.session = session;
        this.nav = nav;
        this.grupoService = grupoService;
        this.usuarioRepository = usuarioRepository; 


        // validar sesión
        validarSesion();

        // Estética y layout
        setSizeFull();
        getStyle().set("background-color", "#E6F7FF");
        setAlignItems(Alignment.CENTER);

        add(nav);

        crearFormulario();
        construirTabla();

        cargar();
    }

    private void crearFormulario() {
        nombreGrupo = new TextField("Nombre del grupo");
        nombreGrupo.setWidth("300px");

        descripcion = new TextField("Descripción");
        descripcion.setWidth("300px");

        // Ojo: llamar a crearGrupo usando la firma de tu servicio:
        // crearGrupo(String nombre, String descripcion, String creadorLogin)
        crearGrupo = new Button("Crear Grupo", VaadinIcon.PLUS_CIRCLE.create(), e -> {
            try {
                Usuario creador = usuarioRepository.findById(session.getLoginEnSesion())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

            grupoService.crearGrupo(
                nombreGrupo.getValue(),
                descripcion.getValue(),
                creador
            );
            Notification.show("Grupo creado exitosamente");
                nombreGrupo.clear();
                descripcion.clear();
                cargar();
            } catch (Exception ex) {
                Notification.show("No fue posible crear el grupo: " + ex.getMessage());
            }
        });

        crearGrupo.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        crearGrupo.getStyle().set("border-radius", "10px");
        crearGrupo.setWidth("300px");

        add(nombreGrupo, descripcion, crearGrupo);
    }

    private void construirTabla() {
        tabla = new Grid<>(Grupo.class, false);
        tabla.removeAllColumns();

        tabla.addColumn(Grupo::getNombreGrupo).setHeader("Grupo").setAutoWidth(true);
        tabla.addColumn(Grupo::getDescripcion).setHeader("Descripción").setAutoWidth(true);

        // columna: número de participantes (segura)
        tabla.addColumn(g -> {
            // evita acceder a colecciones complejas desde la entidad; mejor pedir al servicio si lo prefieres
            try {
                List<String> names = grupoService.obtenerNombresParticipantes(g.getId());
                return names.size();
            } catch (Exception ex) {
                return 0;
            }
        }).setHeader("Participantes");

        // columna de acción: botón "Añadir" que abre diálogo de unirse
        tabla.addComponentColumn(grupo -> {
            Button btn = new Button("Añadir", VaadinIcon.USER_CHECK.create());
            btn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            btn.getStyle().set("border-radius", "8px");
            btn.addClickListener(e -> abrirDialogoUnirse(grupo));
            return btn;
        }).setHeader("Acciones");

        HorizontalLayout tablaContainer = new HorizontalLayout(tabla);
        tablaContainer.setWidth("80%");
        tablaContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        tabla.setWidthFull();

        add(tablaContainer);
    }

    private void cargar() {
        // Usamos el método que tienes: obtenerTodos() o listarGrupos(); aquí llamo a obtenerTodos()
        List<Grupo> grupos = grupoService.obtenerTodos();
        tabla.setItems(grupos);
    }

    private void abrirDialogoUnirse(Grupo grupo) {
        Dialog dialog = new Dialog();
        dialog.setWidth("520px");

        H3 titulo = new H3("Grupo: " + grupo.getNombreGrupo());

        // Caja azul para la descripción
        Paragraph desc = new Paragraph(grupo.getDescripcion());
        desc.getStyle()
                .set("background-color", "#D6ECFF")   // azul suave
                .set("padding", "12px")
                .set("border-radius", "8px")
                .set("width", "100%")
                .set("white-space", "pre-wrap");

        // Lista con iconos + scroll
        VerticalLayout listaContainer = new VerticalLayout();
        listaContainer.setPadding(false);
        listaContainer.setSpacing(false);
        listaContainer.setWidth("100%");
        listaContainer.setHeight("200px");        // altura
        listaContainer.getStyle()
                .set("overflow-y", "auto")        // scroll vertical
                .set("border", "1px solid #ccc")
                .set("border-radius", "6px")
                .set("padding", "6px");

        try {
            List<String> participantes = grupoService.obtenerNombresParticipantes(grupo.getId());
            listaContainer.removeAll();

            for (String nombre : participantes) {
                HorizontalLayout fila = new HorizontalLayout(
                        VaadinIcon.USER.create(),   // ícono
                        new Paragraph(nombre)
                );
                fila.setAlignItems(Alignment.CENTER);
                fila.setSpacing(true);
                listaContainer.add(fila);
            }

        } catch (Exception ex) {
            listaContainer.add(new Paragraph("No se pudieron cargar los participantes."));
        }

        // Botón
        Button btnUnirse = new Button("Unirme", VaadinIcon.PLUS.create(), ev -> {
            String login = session.getLoginEnSesion();
            try {
                grupoService.unirUsuarioAGrupo(login, grupo.getId());
                Notification.show("Te uniste al grupo " + grupo.getNombreGrupo());

                // refrescar participantes
                listaContainer.removeAll();
                grupoService.obtenerNombresParticipantes(grupo.getId()).forEach(nombre -> {
                    HorizontalLayout fila = new HorizontalLayout(
                            VaadinIcon.USER.create(),
                            new Paragraph(nombre)
                    );
                    fila.setAlignItems(Alignment.CENTER);
                    listaContainer.add(fila);
                });

                cargar(); // refrescar tabla principal

            } catch (Exception ex) {
                Notification.show(ex.getMessage());
            }
        });
        btnUnirse.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Contenedor final
        VerticalLayout contenido = new VerticalLayout(titulo, desc, listaContainer, btnUnirse);
        contenido.setPadding(true);

        dialog.add(contenido);
        dialog.open();
    }


    private void validarSesion() {
        if (session.getLoginEnSesion() == null) {
            UI.getCurrent().navigate("login");
        }
    }
}
