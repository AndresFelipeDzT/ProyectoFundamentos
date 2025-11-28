package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;
    private LocalDateTime fechaCreacion;
    private String rutaArchivo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario autor;

    @OneToMany(mappedBy = "publicacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Comentario> comentarios = new HashSet<>();

    public Publicacion() { this.comentarios = new HashSet<>(); }

    public Publicacion(String contenido, Usuario autor) {
        this();
        this.contenido = contenido;
        this.autor = autor;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getContenido() { return contenido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getRutaArchivo() { return rutaArchivo; }
    public Usuario getAutor() { return autor; }
    public Set<Comentario> getComentarios() { return comentarios; }

    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public void setComentarios(Set<Comentario> comentarios) { this.comentarios = comentarios; }

    @Override
    public int hashCode() { return id != null ? id.hashCode() : 0; }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Publicacion other = (Publicacion) obj;
        return id != null && id.equals(other.getId());
    }
}
