package com.ingesoft.redsocial.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@SpringBootTest
class Cu03BuscarPersonaTest {

    @Autowired
    UsuarioRepository usuarios;

    @Autowired
    UsuarioService usuarioService;

    @Test
    void buscarPersonaConResultados() throws Exception {
        
        usuarios.deleteAll();

        Usuario u = new Usuario();
        u.setLogin("juan");
        u.setNombre("Juan Perez");
        u = usuarios.save(u);

        Usuario u2 = new Usuario();
        u2.setLogin("jose");
        u2.setNombre("José Perez");
        u2 = usuarios.save(u2);

        List<Usuario> resultado = usuarioService.buscarPersona("Juan");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("juan", resultado.get(0).getLogin());
    }

    @Test
    void buscarPersonaNoEncontradaLanzaExcepcion() {

        usuarios.deleteAll();

    assertThrows(UsuarioNotFoundException.class, () -> usuarioService.buscarPersona("Xyz"));
    }

    @Test
    void buscarPersonaNullLanzaExcepcion() {
        usuarios.deleteAll();

    assertThrows(UsuarioNotFoundException.class, () -> usuarioService.buscarPersona("Null"));
    }

}
