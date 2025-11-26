package com.ingesoft.redsocial.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ingesoft.redsocial.modelo.PerfilAcademico;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.PerfilAcademicoRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PerfilAcademicoService {

    @Autowired
    PerfilAcademicoRepository perfiles;

    @Autowired
    UsuarioRepository usuarios;

    public PerfilAcademico obtenerPerfil(String login) throws UsuarioNotFoundException {
        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("Usuario no existe");
        }
        return perfiles.findByUsuarioLogin(login);
    }

    public PerfilAcademico actualizarPerfil(String login, String carrera, String semestre, String habilidades)
            throws UsuarioNotFoundException {

        if (!usuarios.existsById(login)) {
            throw new UsuarioNotFoundException("Usuario no existe");
        }

        PerfilAcademico perfil = perfiles.findByUsuarioLogin(login);

        if (perfil == null) {
            perfil = new PerfilAcademico();
            perfil.setUsuario(usuarios.findById(login).get());
        }

        perfil.setCarrera(carrera);
        perfil.setSemestre(semestre);
        perfil.setHabilidades(habilidades);

        return perfiles.save(perfil);
    }
}
