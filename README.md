# Proyecto: Biblioteca API REST

**Descripción breve:** 
Esta API REST permite gestionar el inventario de libros de una biblioteca, implementando operaciones de creación, consulta, actualización y eliminación (CRUD) con persistencia de datos.

**Contexto seleccionado:** Biblioteca.

**Integrantes:** Laura (Desarrollo individual).

**Tecnologías utilizadas:**
* Java 17
* Spring Boot
* Spring Data JPA / Hibernate
* Base de datos H2 (En memoria)
* Maven

**Instrucciones básicas para ejecutar el proyecto:**
1. Clonar el repositorio.
2. Abrir la terminal en la raíz del proyecto.
3. Ejecutar el comando: `.\mvnw clean spring-boot:run`
4. La base de datos H2 se inicializa automáticamente con dos registros de prueba.

**Listado de los principales endpoints:**
* `POST /api/libros` - Crear un nuevo libro.
* `GET /api/libros` - Consultar todos los libros.
* `GET /api/libros/{id}` - Consultar un libro por ID.
* `GET /api/libros/buscar?genero={genero}` - Búsqueda personalizada por género.
* `PUT /api/libros/{id}` - Actualizar un libro existente.
* `DELETE /api/libros/{id}` - Eliminar un libro.

**Ejemplo de una solicitud o respuesta:**
*Petición POST a `/api/libros`:*
```json
{
  "titulo": "Rayuela",
  "autor": "Julio Cortázar",
  "genero": "Novela",
  "anioPublicacion": 1963
}


