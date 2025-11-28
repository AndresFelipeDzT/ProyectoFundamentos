package com.ingesoft.redsocial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // == USUARIOS ==
        Map<String, Usuario> usuarios = new HashMap<>();

        Usuario andres = new Usuario();
        andres.setLogin("andresdiaz5");
        andres.setNombre("Andres Diaz");
        andres.setPassword(passwordEncoder.encode("andresdiaz5*"));
        usuarioRepo.save(andres);
        usuarios.put("andresdiaz5", andres);

        Usuario santiago = new Usuario();
        santiago.setLogin("rayosantiago");
        santiago.setNombre("Santiago Rayo");
        santiago.setPassword(passwordEncoder.encode("rayosantiago3*"));
        usuarioRepo.save(santiago);
        usuarios.put("rayosantiago", santiago);

        Usuario pablo = new Usuario();
        pablo.setLogin("mottat");
        pablo.setNombre("Pablo Motta");
        pablo.setPassword(passwordEncoder.encode("juanpablomotta!"));
        usuarioRepo.save(pablo);
        usuarios.put("mottat", pablo);

        Usuario ana = new Usuario();
        ana.setLogin("romerocana");
        ana.setNombre("Ana Romero");
        ana.setPassword(passwordEncoder.encode("romero20*"));
        usuarioRepo.save(ana);
        usuarios.put("romerocana", ana);

        Usuario jaime = new Usuario();
        jaime.setLogin("jaimec20");
        jaime.setNombre("Jaime Chavarriaga");
        jaime.setPassword(passwordEncoder.encode("jaime2005#"));
        usuarioRepo.save(jaime);
        usuarios.put("jaimec20", jaime);

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
                        "Grupo de básico estudio para las personas que se nos dificulta más base de datos",
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
        publicacionAndres.setRutaImagen(null);
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
            if (!participantesRepo.existsByGrupoIdAndUsuarioLogin(grupo.getId(), u.getLogin())) {
                ParticipantesGrupo p = new ParticipantesGrupo(u, grupo);
                participantesRepo.save(p);
            }
        }
    }
}
