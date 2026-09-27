package com.biblioteca.repository;

import com.biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    // Método mágico para buscar un autor por su nombre en texto
    Optional<Autor> findByNombre(String nombre);
}