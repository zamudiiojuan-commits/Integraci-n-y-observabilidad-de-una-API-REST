package com.biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private String autor;
    
    @Column(nullable = false)
    private String genero;
    
    @Column(name = "anio_publicacion", nullable = false)
    private int anioPublicacion;

    // Constructor vacío OBLIGATORIO para que la base de datos (JPA) funcione
    public Libro() {
    }

    // Constructor sin el 'id' (porque la base de datos lo va a generar automáticamente)
    public Libro(String titulo, String autor, String genero, int anioPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.anioPublicacion = anioPublicacion;
    }

    // Getters y Setters (igual a los que tenías)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
}