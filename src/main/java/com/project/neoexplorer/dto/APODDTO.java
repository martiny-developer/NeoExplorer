package com.project.neoexplorer.dto;

public class APODDTO {

    private String titulo;
    private String imagenUrl;
    private String descripcion;

    public APODDTO() {
    }

    public APODDTO(String titulo, String imagenUrl, String descripcion) {
        this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}