import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.passwordfield.PasswordField;

public class LoginView extends Main {

    private final VerticalLayout registroLayout = new VerticalLayout();

    public LoginView(SessionService session, UsuarioService usuarioService, TituloComponent tituloComponent) {

        // ... tu código actual del loginForm ...

        // Botón para mostrar formulario de registro
        Button botonRegistrar = new Button("Registrar");
        add(botonRegistrar);

        botonRegistrar.addClickListener(event -> mostrarFormularioRegistro());

        // agregar layout para registro, inicialmente vacío
        add(registroLayout);
    }

    private void mostrarFormularioRegistro() {
        registroLayout.removeAll(); // limpia si ya hay algo

        // Campos de registro
        TextField nombre = new TextField("Nombre completo");
        TextField login = new TextField("Nombre de usuario");
        PasswordField password = new PasswordField("Contraseña");

        // Botón para enviar registro
        Button enviar = new Button("Registrar");
        enviar.addClickListener(e -> {
            try {
                usuarioService.registrarNuevoUsuario(login.getValue(), nombre.getValue(), password.getValue());
                Notification.show("Usuario registrado con éxito");
                registroLayout.removeAll(); // limpiar formulario
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        // Agregar todo al layout de registro
        registroLayout.add(nombre, login, password, enviar);
    }
}
