package com.project.neoexplorer.dto;

public class EpicDTO {

    private String fecha;
    private String imagen;
    private String caption;
    private String imageUrl;

    public EpicDTO() {
    }

    public EpicDTO(String fecha, String imagen, String caption, String imageUrl) {
        this.fecha = fecha;
        this.imagen = imagen;
        this.caption = caption;
        this.imageUrl = imageUrl;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}