package com.ingesoft.redsocial;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import jakarta.transaction.Transactional;

@Component
public class InicializadorDatosEjemplo implements CommandLineRunner {

    UsuarioRepository usuarioRepository;

    InicializadorDatosEjemplo(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
   
        // Carga datos iniciales
        usuarioRepository.save(new Usuario("user1", "Usuario 1", "user1"));
        usuarioRepository.save(new Usuario("user2", "Usuario 2", "user2"));
        usuarioRepository.save(new Usuario("user3", "Usuario 3", "user3"));

    }

}
