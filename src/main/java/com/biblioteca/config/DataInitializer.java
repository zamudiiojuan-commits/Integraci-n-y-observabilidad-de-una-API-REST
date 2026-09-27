package com.biblioteca.config;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Libro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LibroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(AutorRepository autorRepository, LibroRepository libroRepository) {
        return args -> {
            // Verificamos si ya hay datos para no duplicarlos al reiniciar
            if (autorRepository.count() == 0) {
                Autor autor1 = new Autor();
                autor1.setNombre("Gabriel García Márquez");
                autor1.setNacionalidad("Colombiana");
                autorRepository.save(autor1);

                Libro libro1 = new Libro();
                libro1.setTitulo("Cien años de soledad");
                libro1.setGenero("Novela");
                libro1.setAnioPublicacion(1967);
                libro1.setAutor(autor1);
                
                libroRepository.save(libro1);
            }
        };
    }
}