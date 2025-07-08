package com.espe.ms_catalogo.dto;

import lombok.Data;

@Data
public class CatalogoDto {
    private String tipo;

    private LibroDto libro;
    private ArticulosCientificosDto articulo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LibroDto getLibro() {
        return libro;
    }

    public void setLibro(LibroDto libro) {
        this.libro = libro;
    }

    public ArticulosCientificosDto getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticulosCientificosDto articulo) {
        this.articulo = articulo;
    }

    public CatalogoDto(String tipo, ArticulosCientificosDto articulo, LibroDto libro) {
        this.tipo = tipo;
        this.articulo = articulo;
        this.libro = libro;
    }

    public CatalogoDto() {
    }
}
