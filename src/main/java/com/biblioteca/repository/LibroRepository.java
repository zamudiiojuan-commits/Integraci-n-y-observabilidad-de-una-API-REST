package com.biblioteca.repository;

import com.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    // Esta es la consulta personalizada que exige tu trabajo.
    // Spring Boot mágicamente crea el código por debajo solo con leer el nombre del método.
    List<Libro> findByGeneroIgnoreCase(String genero);
    
}