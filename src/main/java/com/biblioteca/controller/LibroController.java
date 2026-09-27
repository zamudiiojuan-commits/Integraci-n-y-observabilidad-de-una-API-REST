package com.biblioteca.controller;

import com.biblioteca.dto.LibroRequestDTO;
import com.biblioteca.model.Autor;
import com.biblioteca.model.Libro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LibroRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    // Punto 4: Logs (INFO, WARN, ERROR)
    private static final Logger log = LoggerFactory.getLogger(LibroController.class);

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final MeterRegistry meterRegistry;
    private final RestClient restClient;

    public LibroController(LibroRepository libroRepository,
                           AutorRepository autorRepository,
                           MeterRegistry meterRegistry) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.meterRegistry = meterRegistry;
        // Punto 3: Cliente para consumir la API externa de OpenLibrary
        this.restClient = RestClient.builder()
                .baseUrl("https://openlibrary.org")
                .build();
    }

    // Punto 4: Mecanismo básico de trazabilidad (genera un ID único por solicitud)
    private String generarTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Arma el JSON limpio evitando conflictos de serialización
    private Map<String, Object> convertirAMap(Libro libro) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", libro.getId());
        map.put("titulo", libro.getTitulo());
        map.put("genero", libro.getGenero());
        map.put("anioPublicacion", libro.getAnioPublicacion());

        if (libro.getAutor() != null) {
            Map<String, Object> autorMap = new LinkedHashMap<>();
            autorMap.put("id", libro.getAutor().getId());
            autorMap.put("nombre", libro.getAutor().getNombre());
            autorMap.put("nacionalidad", libro.getAutor().getNacionalidad());
            map.put("autor", autorMap);
        } else {
            map.put("autor", null);
        }
        return map;
    }

    // 1. CREAR (POST)
    @PostMapping
    public ResponseEntity<?> crearLibro(@RequestBody LibroRequestDTO dto) {
        String traceId = generarTraceId();
        log.info("[TraceId: {}] Solicitud POST para registrar el libro: {}", traceId, dto.titulo());

        try {
            Autor autor = autorRepository.findByNombre(dto.autor())
                    .orElseGet(() -> {
                        log.info("[TraceId: {}] Creando nuevo autor en MySQL: {}", traceId, dto.autor());
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(dto.autor());
                        nuevoAutor.setNacionalidad("Por definir");
                        return autorRepository.save(nuevoAutor);
                    });

            Libro nuevoLibro = new Libro(dto.titulo(), autor, dto.genero(), dto.anioPublicacion());
            Libro libroGuardado = libroRepository.save(nuevoLibro);

            // Punto 4: Métrica personalizada de libros creados
            meterRegistry.counter("biblioteca.libros.creados").increment();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("X-Trace-Id", traceId)
                    .body(convertirAMap(libroGuardado));
        } catch (Throwable e) {
            log.error("[TraceId: {}] Error al crear el libro: {}", traceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Trace-Id", traceId)
                    .body(Map.of("error", "Error al guardar el libro", "detalle", String.valueOf(e.getMessage())));
        }
    }

    // 2. CONSULTAR TODOS (GET)
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        String traceId = generarTraceId();
        // Punto 4: Métrica personalizada de consultas realizadas
        meterRegistry.counter("biblioteca.libros.consultas").increment();
        log.info("[TraceId: {}] Consultando listado completo de libros en MySQL", traceId);

        try {
            List<Libro> libros = libroRepository.findAll();
            List<Map<String, Object>> respuesta = new ArrayList<>();
            for (Libro libro : libros) {
                respuesta.add(convertirAMap(libro));
            }
            return ResponseEntity.ok()
                    .header("X-Trace-Id", traceId)
                    .body(respuesta);
        } catch (Throwable e) {
            log.error("[TraceId: {}] Error consultando libros: {}", traceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    // 3. CONSULTAR POR ID (GET /{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        String traceId = generarTraceId();
        meterRegistry.counter("biblioteca.libros.consultas").increment();
        log.info("[TraceId: {}] Buscando libro con ID: {}", traceId, id);

        Optional<Libro> libro = libroRepository.findById(id);
        if (libro.isPresent()) {
            return ResponseEntity.ok()
                    .header("X-Trace-Id", traceId)
                    .body(convertirAMap(libro.get()));
        } else {
            log.warn("[TraceId: {}] No se encontró el libro con ID: {}", traceId, id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("X-Trace-Id", traceId)
                    .body(Map.of("mensaje", "No existe un libro con el ID " + id, "traceId", traceId));
        }
    }

    // 4. CONSULTA PERSONALIZADA POR GÉNERO (GET /buscar?genero=...)
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorGenero(@RequestParam String genero) {
        String traceId = generarTraceId();
        meterRegistry.counter("biblioteca.libros.busquedas.genero").increment();
        log.info("[TraceId: {}] Buscando libros del género: {}", traceId, genero);

        List<Libro> libros = libroRepository.findByGeneroIgnoreCase(genero);
        if (libros.isEmpty()) {
            log.warn("[TraceId: {}] No se encontraron libros para el género: {}", traceId, genero);
        }

        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Libro libro : libros) {
            respuesta.add(convertirAMap(libro));
        }
        return ResponseEntity.ok()
                .header("X-Trace-Id", traceId)
                .body(respuesta);
    }

    // 5. CONSUMO DE API EXTERNA CON MANEJO DE ERRORES (Punto 3 de la rúbrica)
    // Ejemplo de uso: http://localhost:8081/api/libros/externo?titulo=quijote
    @GetMapping("/externo")
    public ResponseEntity<?> consultarApiExterna(@RequestParam(defaultValue = "") String titulo) {
        String traceId = generarTraceId();
        meterRegistry.counter("biblioteca.api.externa.solicitudes").increment();

        // Manejo controlado de error 1: parámetro vacío
        if (titulo == null || titulo.trim().isEmpty()) {
            log.warn("[TraceId: {}] Intento de consulta a API externa con título vacío", traceId);
            return ResponseEntity.badRequest()
                    .header("X-Trace-Id", traceId)
                    .body(Map.of(
                            "error", "Parámetro inválido",
                            "mensaje", "Debes enviar un título válido. Ejemplo: /api/libros/externo?titulo=quijote",
                            "traceId", traceId
                    ));
        }

        try {
            log.info("[TraceId: {}] Consumiendo API externa OpenLibrary para el título: {}", traceId, titulo);

            @SuppressWarnings("unchecked")
            Map<String, Object> respuestaExterna = restClient.get()
                    .uri("/search.json?title={titulo}&limit=3", titulo)
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> resultadoProcesado = new LinkedHashMap<>();
            resultadoProcesado.put("fuenteExterna", "OpenLibrary API (https://openlibrary.org)");
            resultadoProcesado.put("terminoBuscado", titulo);
            resultadoProcesado.put("traceId", traceId);
            resultadoProcesado.put("totalEncontradosEnAPI", respuestaExterna != null ? respuestaExterna.get("numFound") : 0);
            resultadoProcesado.put("librosLocalesEnMySQL", libroRepository.count());
            resultadoProcesado.put("resultadosExternos", respuestaExterna != null ? respuestaExterna.get("docs") : List.of());

            return ResponseEntity.ok()
                    .header("X-Trace-Id", traceId)
                    .body(resultadoProcesado);

        } catch (Exception e) {
            // Manejo controlado de error 2: caída o fallo del servicio externo
            meterRegistry.counter("biblioteca.api.externa.errores").increment();
            log.error("[TraceId: {}] Error al comunicarse con la API externa OpenLibrary: {}", traceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("X-Trace-Id", traceId)
                    .body(Map.of(
                            "error", "Servicio externo no disponible temporalmente",
                            "detalle", e.getMessage(),
                            "traceId", traceId
                    ));
        }
    }

    // 6. ACTUALIZAR (PUT /{id})
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLibro(@PathVariable Long id, @RequestBody LibroRequestDTO dto) {
        String traceId = generarTraceId();
        log.info("[TraceId: {}] Solicitud PUT para actualizar libro ID: {}", traceId, id);

        Optional<Libro> libroExistente = libroRepository.findById(id);
        if (libroExistente.isPresent()) {
            Autor autor = autorRepository.findByNombre(dto.autor())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(dto.autor());
                        nuevoAutor.setNacionalidad("Por definir");
                        return autorRepository.save(nuevoAutor);
                    });

            Libro libroAActualizar = libroExistente.get();
            libroAActualizar.setTitulo(dto.titulo());
            libroAActualizar.setAutor(autor);
            libroAActualizar.setGenero(dto.genero());
            libroAActualizar.setAnioPublicacion(dto.anioPublicacion());

            Libro actualizado = libroRepository.save(libroAActualizar);
            return ResponseEntity.ok()
                    .header("X-Trace-Id", traceId)
                    .body(convertirAMap(actualizado));
        } else {
            log.warn("[TraceId: {}] No se pudo actualizar. Libro no encontrado con ID: {}", traceId, id);
            return ResponseEntity.notFound().build();
        }
    }

    // 7. ELIMINAR (DELETE /{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        String traceId = generarTraceId();
        if (libroRepository.existsById(id)) {
            libroRepository.deleteById(id);
            log.info("[TraceId: {}] Libro eliminado correctamente. ID: {}", traceId, id);
            return ResponseEntity.noContent().header("X-Trace-Id", traceId).build();
        } else {
            log.warn("[TraceId: {}] Intento de eliminar un libro inexistente. ID: {}", traceId, id);
            return ResponseEntity.notFound().build();
        }
    }
}