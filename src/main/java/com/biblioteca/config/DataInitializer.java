package com.biblioteca.config;

import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(LibroRepository repository) {
        return args -> {
            // Guardamos dos libros de prueba automáticamente al iniciar
            repository.save(new Libro("Cien Años de Soledad", "Gabriel García Márquez", "Realismo Mágico", 1967));
            repository.save(new Libro("El Principito", "Antoine de Saint-Exupéry", "Fábula", 1943));
        };
    }
}