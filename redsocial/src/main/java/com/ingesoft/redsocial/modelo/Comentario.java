package com.ingesoft.redsocial.modelo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto;
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Publicacion publicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Comentario comentarioPadre; 

    @OneToMany(mappedBy = "comentarioPadre", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Comentario> respuestas = new HashSet<>();

    @OneToMany(mappedBy = "comentario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Reaccion> reacciones = new HashSet<>();

    public Comentario() {
        this.respuestas = new HashSet<>();
        this.reacciones = new HashSet<>();
    }

    public Comentario(String texto, Usuario autor, Publicacion publicacion) {
        this();
        this.texto = texto;
        this.autor = autor;
        this.publicacion = publicacion;
        this.fecha = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() { return id; }
    public String getTexto() { return texto; }
    public LocalDateTime getFecha() { return fecha; }
    public Usuario getAutor() { return autor; }
    public Publicacion getPublicacion() { return publicacion; }
    public Comentario getComentarioPadre() { return comentarioPadre; }
    public Set<Comentario> getRespuestas() { return respuestas; }
    public Set<Reaccion> getReacciones() { return reacciones; }

    public void setTexto(String texto) { this.texto = texto; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
    public void setComentarioPadre(Comentario comentarioPadre) { this.comentarioPadre = comentarioPadre; }
    public void setRespuestas(Set<Comentario> respuestas) { this.respuestas = respuestas; }
    public void setReacciones(Set<Reaccion> reacciones) { this.reacciones = reacciones; }

    @Override
    public int hashCode() { return id != null ? id.hashCode() : 0; }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Comentario other = (Comentario) obj;
        return id != null && id.equals(other.getId());
    }
}
