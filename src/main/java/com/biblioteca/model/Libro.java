package com.biblioteca.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "libros")
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    @JsonIgnore // <-- Cambiamos a JsonIgnore para evitar el conflicto de serialización
    private Autor autor;
    
    @Column(nullable = false)
    private String genero;
    
    @Column(name = "anio_publicacion", nullable = false)
    private int anioPublicacion;

    public Libro() {}

    public Libro(String titulo, Autor autor, String genero, int anioPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.anioPublicacion = anioPublicacion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
}