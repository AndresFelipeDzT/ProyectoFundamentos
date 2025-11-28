package com.ingesoft.redsocial.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reaccion {

    public enum TipoReaccion {
        LIKE, DISLIKE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comentario comentario;

    @Enumerated(EnumType.STRING)
    private TipoReaccion tipo;

    // ---------------- Setters y Getters manuales ----------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Comentario getComentario() { return comentario; }
    public void setComentario(Comentario comentario) { this.comentario = comentario; }

    public TipoReaccion getTipo() { return tipo; }
    public void setTipo(TipoReaccion tipo) { this.tipo = tipo; }
}
