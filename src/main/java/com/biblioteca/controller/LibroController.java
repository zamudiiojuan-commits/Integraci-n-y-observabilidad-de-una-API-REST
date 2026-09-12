package com.biblioteca.controller;

import com.biblioteca.dto.LibroRequestDTO; // Asegúrate de que tienes este DTO de la unidad pasada
import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    // Aquí inyectamos nuestra base de datos (el repositorio)
    private final LibroRepository libroRepository;

    public LibroController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    // 1. CREAR (POST) -> Guarda en la base de datos y retorna 201 Created
    @PostMapping
    public ResponseEntity<Libro> crearLibro(@RequestBody LibroRequestDTO dto) {
        Libro nuevoLibro = new Libro(dto.titulo(), dto.autor(), dto.genero(), dto.anioPublicacion());
        Libro libroGuardado = libroRepository.save(nuevoLibro);
        return ResponseEntity.status(HttpStatus.CREATED).body(libroGuardado);
    }

    // 2. CONSULTAR TODOS (GET) -> Retorna 200 OK
    @GetMapping
    public ResponseEntity<List<Libro>> listarTodos() {
        return ResponseEntity.ok(libroRepository.findAll());
    }

    // 3. CONSULTAR POR ID (GET /{id}) -> Retorna 200 OK o 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscarPorId(@PathVariable Long id) {
        Optional<Libro> libro = libroRepository.findById(id);
        
        if (libro.isPresent()) {
            return ResponseEntity.ok(libro.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. CONSULTA PERSONALIZADA (GET /buscar?genero=Novela) -> Retorna 200 OK
    @GetMapping("/buscar")
    public ResponseEntity<List<Libro>> buscarPorGenero(@RequestParam String genero) {
        List<Libro> libros = libroRepository.findByGeneroIgnoreCase(genero);
        return ResponseEntity.ok(libros);
    }

    // 5. ACTUALIZAR (PUT /{id}) -> Retorna 200 OK o 404 Not Found
    @PutMapping("/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @RequestBody LibroRequestDTO dto) {
        Optional<Libro> libroExistente = libroRepository.findById(id);
        
        if (libroExistente.isPresent()) {
            Libro libroAActualizar = libroExistente.get();
            // Actualizamos los datos
            libroAActualizar.setTitulo(dto.titulo());
            libroAActualizar.setAutor(dto.autor());
            libroAActualizar.setGenero(dto.genero());
            libroAActualizar.setAnioPublicacion(dto.anioPublicacion());
            
            // Guardamos los cambios
            Libro actualizado = libroRepository.save(libroAActualizar);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. ELIMINAR (DELETE /{id}) -> Retorna 204 No Content o 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        if (libroRepository.existsById(id)) {
            libroRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}