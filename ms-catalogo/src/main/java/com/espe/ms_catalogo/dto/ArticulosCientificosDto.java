package com.espe.ms_catalogo.dto;

import lombok.*;

import java.util.Date;

@Data
@Setter
@Getter

public class ArticulosCientificosDto {
    private String titulo;
    private int anioPublicacion;
    private String resumen;
    private String editorial;
    private String isbn;
    private String orcid;
    private Date fechaPublicacion;
    private String revista;
    private String areaInvestigacion;
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

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getRevista() {
        return revista;
    }

    public void setRevista(String revista) {
        this.revista = revista;
    }

    public String getAreaInvestigacion() {
        return areaInvestigacion;
    }

    public void setAreaInvestigacion(String areaInvestigacion) {
        this.areaInvestigacion = areaInvestigacion;
    }

    public Long getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Long idAutor) {
        this.idAutor = idAutor;
    }

    public ArticulosCientificosDto(String titulo, int anioPublicacion, String resumen, String editorial, String isbn, String orcid, Date fechaPublicacion, String revista, String areaInvestigacion, Long idAutor) {
        this.titulo = titulo;
        this.anioPublicacion = anioPublicacion;
        this.resumen = resumen;
        this.editorial = editorial;
        this.isbn = isbn;
        this.orcid = orcid;
        this.fechaPublicacion = fechaPublicacion;
        this.revista = revista;
        this.areaInvestigacion = areaInvestigacion;
        this.idAutor = idAutor;
    }

    public ArticulosCientificosDto() {
    }
}
