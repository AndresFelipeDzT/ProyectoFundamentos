package com.ingesoft.redsocial.servicios;

import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.GrupoRepository;
import com.ingesoft.redsocial.repositorios.ParticipantesGrupoRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipantesGrupoRepository participantesRepo;

    // Crear grupo (ejemplo sencillo)
    @Transactional
   public Grupo crearGrupo(String nombre, String descripcion, Usuario creador) {

        if (grupoRepository.findByNombreGrupoIgnoreCase(nombre).isPresent()) {
            throw new RuntimeException("El grupo ya existe");
        }

        Grupo g = new Grupo();
        g.setNombreGrupo(nombre);
        g.setDescripcion(descripcion);
        g.setCreador(creador);  // 👈 IMPORTANTE

        return grupoRepository.save(g);
    }
    // Devuelve todos los grupos (se puede renombrar listarGrupos)
    @Transactional(readOnly = true)
    public List<Grupo> listarGrupos() {
        return grupoRepository.findAll();
    }

    // Alternativa de nombre que estabas llamando desde la UI
    public List<Grupo> obtenerTodos() {
        return listarGrupos();
    }

    // Obtener nombres de participantes dentro de la transacción
    @Transactional(readOnly = true)
    public List<String> obtenerNombresParticipantes(Long grupoId) {
        return participantesRepo.findByGrupoId(grupoId)
                .stream()
                .map(pg -> pg.getUsuario().getLogin())
                .toList();
    }

    // Método público para unir un usuario (nombre 'unirUsuarioAGrupo' que estabas llamando)
    @Transactional
    public void unirUsuarioAGrupo(String loginUsuario, Long grupoId) {
        boolean existe = participantesRepo.existsByGrupoIdAndUsuarioLogin(grupoId, loginUsuario);
        if (existe) {
            throw new RuntimeException("El usuario ya pertenece al grupo");
        }

        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Usuario usuario = usuarioRepository.findById(loginUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ParticipantesGrupo nuevo = new ParticipantesGrupo(usuario, grupo);
        participantesRepo.save(nuevo);
    }

    // Método alternativo sinónimo (por si tu código lo usa)
    @Transactional
    public void agregarParticipante(Long grupoId, String loginUsuario) {
        unirUsuarioAGrupo(loginUsuario, grupoId);
    }
}
