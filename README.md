# 📚 API REST de Biblioteca - Integración y Observabilidad

## 1. Descripción de la solución
API REST desarrollada con Java y Spring Boot para la gestión integral de una biblioteca. La solución evoluciona incorporando persistencia relacional en **MySQL**, relación uno a muchos (`1:N`) entre **Autores y Libros**, integración con el servicio externo **OpenLibrary API** mediante `RestClient`, y mecanismos de observabilidad con **Spring Boot Actuator**, métricas personalizadas para **Prometheus**, trazabilidad por solicitud (`X-Trace-Id`) y registro de eventos (`Logs`).

## 2. Integrantes
* **Juan** - Desarrollo individual (Ingeniería de Sistemas)

## 3. Tecnologías utilizadas
* **Java 17** y **Maven**
* **Spring Boot 4.0.5** (`Spring Web`, `Spring Data JPA`)
* **MySQL** (`mysql-connector-j`)
* **Spring Boot Actuator** y **Micrometer Registry Prometheus**
* **RestClient** (Consumo de API REST externa)
* **SLF4J / Logback** (Gestión de logs `INFO`, `WARN`, `ERROR`)

## 4. Configuración necesaria para MySQL
La aplicación utiliza la base de datos `biblioteca_db` (la cual se crea automáticamente si no existe). Para proteger las credenciales sensibles, la conexión admite variables de entorno en `application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/biblioteca_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASS:tu_password_mysql}