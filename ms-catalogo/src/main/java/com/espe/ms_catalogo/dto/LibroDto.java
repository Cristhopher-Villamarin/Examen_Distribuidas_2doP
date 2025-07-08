package com.espe.ms_catalogo.dto;

import lombok.*;

@Setter
@Getter
@Data

public class LibroDto {
    private String titulo;
    private int anioPublicacion;
    private String resumen;
    private String editorial;
    private String isbn;
    private String genero;
    private int numeroPaginas;
    private Long idAutor;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public Long getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Long idAutor) {
        this.idAutor = idAutor;
    }

    public LibroDto(String titulo, int anioPublicacion, String resumen, String editorial, String isbn, String genero, int numeroPaginas, Long idAutor) {
        this.titulo = titulo;
        this.anioPublicacion = anioPublicacion;
        this.resumen = resumen;
        this.editorial = editorial;
        this.isbn = isbn;
        this.genero = genero;
        this.numeroPaginas = numeroPaginas;
        this.idAutor = idAutor;
    }

    public LibroDto() {
    }
}
