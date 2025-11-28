package com.ingesoft.redsocial;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ingesoft.redsocial.modelo.*;
import com.ingesoft.redsocial.repositorios.*;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class InicializadorDatosEjemplo implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final GrupoRepository grupoRepo;
    private final ParticipantesGrupoRepository participantesRepo;
    private final PublicacionRepository publicacionRepo;
    private final ComentarioRepository comentarioRepo;

    public InicializadorDatosEjemplo(
            UsuarioRepository usuarioRepo,
            GrupoRepository grupoRepo,
            ParticipantesGrupoRepository participantesRepo,
            PublicacionRepository publicacionRepo,
            ComentarioRepository comentarioRepo) {
        this.usuarioRepo = usuarioRepo;
        this.grupoRepo = grupoRepo;
        this.participantesRepo = participantesRepo;
        this.publicacionRepo = publicacionRepo;
        this.comentarioRepo = comentarioRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // == USUARIOS ==
        Map<String, Usuario> usuarios = new HashMap<>();

        usuarios.put("andresdiaz5", new Usuario("andresdiaz5", "Andres Diaz", "andresdiaz5*"));
        usuarios.put("rayosantiago", new Usuario("rayosantiago", "Santiago Rayo", "rayosantiago3*"));
        usuarios.put("mottat", new Usuario("mottat", "Pablo Motta", "juanpablomotta!"));
        usuarios.put("romerocana", new Usuario("romerocana", "Ana Romero", "romero20*"));
        usuarios.put("jaimec20", new Usuario("jaimec20", "Jaime Chavarriaga", "jaime2005#"));

        // Guardar usuarios
        usuarios.values().forEach(u -> usuarioRepo.save(u));

        // == GRUPOS ==
        Map<String, Grupo> grupos = new HashMap<>();

        grupos.put("Fundamentos en Ingeniería de Software",
                crearGrupo("Fundamentos en Ingeniería de Software",
                        "Grupo de estudio para FIS",
                        usuarios.get("jaimec20")));

        grupos.put("Estructura de datos",
                crearGrupo("Estructura de datos",
                        "Grupo de estudio para estructuras",
                        usuarios.get("romerocana")));

        grupos.put("Base de datos",
                crearGrupo("Base de datos",
                        "Grupo de básico estudio para las personas que se nos dificulta mas base de datos",
                        usuarios.get("jaimec20")));

        grupos.put("Gestion e Innovación de TI",
                crearGrupo("Gestion e Innovación de TI",
                        "Grupo para expertos y posibles emprendedores",
                        usuarios.get("andresdiaz5")));

        // == PARTICIPANTES DE LOS GRUPOS ==
        unirParticipantes(grupos.get("Fundamentos en Ingeniería de Software"),
                usuarios.get("jaimec20"), usuarios.get("mottat"));

        unirParticipantes(grupos.get("Estructura de datos"),
                usuarios.get("romerocana"));

        unirParticipantes(grupos.get("Base de datos"),
                usuarios.get("jaimec20"), usuarios.get("andresdiaz5"));

        unirParticipantes(grupos.get("Gestion e Innovación de TI"),
                usuarios.get("andresdiaz5"), usuarios.get("rayosantiago"));

       // == PUBLICACIONES ==
        Publicacion publicacionAndres = new Publicacion();
        publicacionAndres.setAutor(usuarios.get("andresdiaz5"));
        publicacionAndres.setContenido("Hola alguien tiene el parcial 3 de Base de datos");
        publicacionAndres.setFechaCreacion(LocalDateTime.now());
        publicacionAndres.setRutaImagen(null); // opcional si no hay imagen
        publicacionRepo.save(publicacionAndres);


        // == COMENTARIOS ==
        Comentario comentarioSantiago = new Comentario();
        comentarioSantiago.setAutor(usuarios.get("rayosantiago"));
        comentarioSantiago.setPublicacion(publicacionAndres);
        comentarioSantiago.setTexto("Yo lo tengo, escríbeme al +57 1234567890");
        comentarioSantiago.setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        comentarioRepo.save(comentarioSantiago);


        System.out.println("Datos de ejemplo cargados correctamente!");
    }

    private Grupo crearGrupo(String nombre, String descripcion, Usuario creador) {
        Grupo g = new Grupo();
        g.setNombreGrupo(nombre);
        g.setDescripcion(descripcion);
        g.setCreador(creador);
        Grupo guardado = grupoRepo.save(g);

        // Agregar creador como participante
        ParticipantesGrupo p = new ParticipantesGrupo(creador, guardado);
        participantesRepo.save(p);

        return guardado;
    }

    private void unirParticipantes(Grupo grupo, Usuario... usuarios) {
        for (Usuario u : usuarios) {
            // Evita duplicados si ya existe
            if (!participantesRepo.existsByGrupoIdAndUsuarioLogin(grupo.getId(), u.getLogin())) {
                ParticipantesGrupo p = new ParticipantesGrupo(u, grupo);
                participantesRepo.save(p);
            }
        }
    }
}
