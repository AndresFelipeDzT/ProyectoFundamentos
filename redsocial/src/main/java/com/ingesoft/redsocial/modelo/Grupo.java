package com.ingesoft.redsocial.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreGrupo;
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_login")
    private Usuario creador;

    // colección LAZY: OK, pero NO permitirá que la UI la inicialice directamente
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ParticipantesGrupo> participantes = new ArrayList<>();

    // Campo transitorio que contendrá el número de participantes
    // Se rellenará desde el servicio (dentro de la transacción)
    @Transient
    private Integer cantidadParticipantes;

    public Grupo() {}

    public Long getId() { return id; }

    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }

    // Getter del listado (no se usa en la UI directamente)
    public List<ParticipantesGrupo> getParticipantes() { return participantes; }
    public void setParticipantes(List<ParticipantesGrupo> participantes) { this.participantes = participantes; }

    // Getter seguro para que Vaadin lea sólo el valor primitivo (sin tocar la colección LAZY)
    public int getCantidadParticipantes() {
        // Si el servicio ya llenó cantidadParticipantes, devolverlo.
        // Si no, devolver 0 (NO acceder a participantes.size() aquí).
        return cantidadParticipantes != null ? cantidadParticipantes : 0;
    }

    // Setter que el servicio usará para inicializar el valor dentro de la transacción
    public void setCantidadParticipantes(Integer cantidadParticipantes) {
        this.cantidadParticipantes = cantidadParticipantes;
    }
}
