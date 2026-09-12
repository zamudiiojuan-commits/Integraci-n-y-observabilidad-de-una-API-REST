package com.biblioteca.dto;

public record LibroRequestDTO(
    String titulo,
    String autor,
    String genero,
    int anioPublicacion
) {}