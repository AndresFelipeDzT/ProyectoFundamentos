package com.ingesoft.redsocial.servicios;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ingesoft.redsocial.excepciones.GrupoExistenteException;
import com.ingesoft.redsocial.excepciones.GrupoNotFoundException;
import com.ingesoft.redsocial.excepciones.UsuarioAlreadyInGroupException;
import com.ingesoft.redsocial.excepciones.UsuarioNotFoundException;
import com.ingesoft.redsocial.modelo.Grupo;
import com.ingesoft.redsocial.modelo.ParticipantesGrupo;
import com.ingesoft.redsocial.modelo.Usuario;
import com.ingesoft.redsocial.repositorios.GrupoRepository;
import com.ingesoft.redsocial.repositorios.UsuarioRepository;

@Service
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepo;
    private final UsuarioRepository usuarioRepo;

    public GrupoService(GrupoRepository grupoRepo, UsuarioRepository usuarioRepo) {
        this.grupoRepo = grupoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Crear grupo: dentro de la transacción, inicializa participantes y setea el contador
    public Grupo crearGrupo(String loginCreador, String nombre, String descripcion)
            throws UsuarioNotFoundException, GrupoExistenteException {

        if (grupoRepo.findByNombreGrupoIgnoreCase(nombre).isPresent()) {
            throw new GrupoExistenteException("Ya existe un grupo con ese nombre");
        }

        Usuario creador = usuarioRepo.findById(loginCreador)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setCreador(creador);

        // Agregar creador como participante
        ParticipantesGrupo participanteCreador = new ParticipantesGrupo(creador, grupo);
        grupo.getParticipantes().add(participanteCreador);

        Grupo guardado = grupoRepo.save(grupo);

        // Inicializar el contador (importante: estamos dentro de la transacción)
        guardado.setCantidadParticipantes(guardado.getParticipantes().size());

        return guardado;
    }

    // Unirse a grupo: añade participante y actualiza contador
    public void unirseAGrupo(String loginUsuario, Long grupoId)
            throws UsuarioNotFoundException, GrupoNotFoundException, UsuarioAlreadyInGroupException {

        Grupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado"));

        Usuario usuario = usuarioRepo.findById(loginUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        boolean yaParticipa = grupo.getParticipantes().stream()
                .anyMatch(p -> p.getUsuario().getLogin().equals(loginUsuario));

        if (yaParticipa) {
            throw new UsuarioAlreadyInGroupException("Ya eres miembro de este grupo");
        }

        ParticipantesGrupo nuevo = new ParticipantesGrupo(usuario, grupo);
        grupo.getParticipantes().add(nuevo);

        // Guardamos el grupo y actualizamos el contador dentro de la transacción
        Grupo guardado = grupoRepo.save(grupo);
        guardado.setCantidadParticipantes(guardado.getParticipantes().size());
    }

    // Listar todos: IMPORTANTÍSIMO — inicializa el contador para cada grupo
    public List<Grupo> listarTodos() {
        List<Grupo> grupos = grupoRepo.findAll();

        // Estamos dentro de la transacción: podemos leer participantes.size() sin error
        for (Grupo g : grupos) {
            int size = (g.getParticipantes() != null) ? g.getParticipantes().size() : 0;
            g.setCantidadParticipantes(size);
        }

        return grupos;
    }

    // Obtener nombres de participantes (devuelve Strings; se ejecuta dentro de transacción)
    public List<String> obtenerNombresParticipantes(Long grupoId) throws GrupoNotFoundException {
        Grupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo no encontrado"));

        return grupo.getParticipantes().stream()
                .map(p -> p.getUsuario().getLogin())
                .collect(Collectors.toList());
    }
}
